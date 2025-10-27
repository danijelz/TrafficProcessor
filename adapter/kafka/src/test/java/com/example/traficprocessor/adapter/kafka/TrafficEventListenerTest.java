package com.example.traficprocessor.adapter.kafka;

import static com.example.traficprocessor.adapter.kafka.KafkaConstants.DEDUPLICATED_TRAFFIC_EVENTS_DLT_TOPIC;
import static com.example.traficprocessor.adapter.kafka.KafkaConstants.DEDUPLICATED_TRAFFIC_EVENTS_RETRY_TOPIC;
import static com.example.traficprocessor.adapter.kafka.KafkaConstants.DEDUPLICATED_TRAFFIC_EVENTS_TOPIC;
import static com.example.traficprocessor.adapter.kafka.KafkaConstants.TRAFFIC_EVENTS_DLT_TOPIC;
import static com.example.traficprocessor.adapter.kafka.KafkaConstants.TRAFFIC_EVENTS_TOPIC;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomInt;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomString;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.range;
import static java.util.stream.StreamSupport.stream;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.kafka.test.utils.KafkaTestUtils.consumerProps;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getRecords;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.example.traficprocessor.core.domain.TrafficProcessorService;
import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.utils.Values;
import com.example.traficprocessor.core.model.TrafficEvent;
import java.util.ArrayList;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@EmbeddedKafka(
    kraft = true,
    partitions = 1,
    topics = {
      TRAFFIC_EVENTS_TOPIC,
      TRAFFIC_EVENTS_DLT_TOPIC,
      DEDUPLICATED_TRAFFIC_EVENTS_TOPIC,
      DEDUPLICATED_TRAFFIC_EVENTS_RETRY_TOPIC,
      DEDUPLICATED_TRAFFIC_EVENTS_DLT_TOPIC
    })
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {KafkaConfig.class, KafkaTestConfig.class})
@TestPropertySource(properties = "spring.kafka.streams.application-id=trafficProcessor")
public class TrafficEventListenerTest {
  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
  @Autowired private KafkaTemplate<String, Object> kafkaTemplate;
  @Autowired private ProducerFactory<?, ?> producerFactory;
  @MockitoBean private TrafficProcessorService trafficProcessorService;

  @Test
  void
      givenValidTrafficEvent_WhenSentToDeduplicatedTrafficEventsTopic_ThenTrafficEventIsPushedToTrafficProcessorService() {
    var recordedEevents = new ArrayList<TrafficEvent>();
    when(trafficProcessorService.processTrafficEvent(any()))
        .then(
            invocation -> {
              var trafficEvent = invocation.<TrafficEvent>getArgument(0);
              recordedEevents.add(trafficEvent);
              return trafficEvent.toId();
            });

    var trafficEvent = Instancio.create(KafkaTrafficEvent.class);
    kafkaTemplate.send(TRAFFIC_EVENTS_TOPIC, trafficEvent);

    await()
        .atMost(10, SECONDS)
        .pollDelay(ofMillis(300))
        .ignoreExceptions()
        .until(() -> recordedEevents.size(), greaterThan(0));

    assertThat(recordedEevents).hasSize(1);
  }

  @Test
  void
      givenDuplicateValidTrafficEvents_WhenSentToTrafficEventsTopic_ThenDuplicateTrafficEventsAreNotPushedToTrafficProcessorService() {
    var recordedEevents = new ArrayList<TrafficEvent>();
    when(trafficProcessorService.processTrafficEvent(any()))
        .then(
            invocation -> {
              var trafficEvent = invocation.<TrafficEvent>getArgument(0);
              recordedEevents.add(trafficEvent);
              return trafficEvent.toId();
            });

    var trafficEvent = Instancio.create(KafkaTrafficEvent.class);
    range(0, randomInt(11, 17))
        .forEach(_ -> kafkaTemplate.send(TRAFFIC_EVENTS_TOPIC, trafficEvent));

    trafficEvent.setVehicleId(trafficEvent.getVehicleId() + randomString());
    kafkaTemplate.send(TRAFFIC_EVENTS_TOPIC, trafficEvent);

    await()
        .atMost(10, SECONDS)
        .pollDelay(ofMillis(300))
        .ignoreExceptions()
        .until(() -> recordedEevents.size(), greaterThan(1));

    assertThat(recordedEevents).hasSize(2);
  }

  @Test
  void
      givenValidTrafficEvent_WhenSentToTrafficEventsTopic_ThenTrafficEventIsPassedToDeduplicatedTrafficEventsTopic() {
    var trafficEvent = Instancio.create(KafkaTrafficEvent.class);
    kafkaTemplate.send(TRAFFIC_EVENTS_TOPIC, trafficEvent);

    try (var consumer = createTestConsumer()) {
      embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
      var records = getRecords(consumer, ofSeconds(10), 2);
      var topics = stream(records.spliterator(), false).map(ConsumerRecord::topic).toList();

      assertThat(topics)
          .containsExactlyInAnyOrder(TRAFFIC_EVENTS_TOPIC, DEDUPLICATED_TRAFFIC_EVENTS_TOPIC);
    }
  }

  @Test
  void
      givenInvalidTrafficEvent_WhenSentToTrafficEventsTopic_ThenTrafficEventIsPassedToDltHandler() {
    var trafficEvent =
        Instancio.of(KafkaTrafficEvent.class)
            .generate(field("timestamp"), gen -> gen.longs().max(-1l))
            .create();
    kafkaTemplate.send(TRAFFIC_EVENTS_TOPIC, trafficEvent);

    try (var consumer = createTestConsumer()) {
      embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
      var records = getRecords(consumer, ofSeconds(10), 3);
      var topics = stream(records.spliterator(), false).map(ConsumerRecord::topic).toList();

      assertThat(topics)
          .containsExactlyInAnyOrder(
              TRAFFIC_EVENTS_TOPIC,
              DEDUPLICATED_TRAFFIC_EVENTS_TOPIC,
              DEDUPLICATED_TRAFFIC_EVENTS_DLT_TOPIC);
    }
  }

  @Test
  void
      givenMalformedTrafficEvent_WhenSentToTrafficEventsTopic_ThenTrafficEventIsPassedToDltHandler() {
    var template =
        new KafkaTemplate<>(
            producerFactory,
            Map.of(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()));
    template.send(TRAFFIC_EVENTS_TOPIC, Values.cast(randomString()));

    var consumerProps = consumerProps(getClass().getName(), "true", embeddedKafkaBroker);
    consumerProps.put(AUTO_OFFSET_RESET_CONFIG, "earliest");

    var stringDeserializer = new StringDeserializer();
    var factory =
        new DefaultKafkaConsumerFactory<>(consumerProps, stringDeserializer, stringDeserializer);

    try (var consumer = factory.createConsumer()) {
      embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
      var records = getRecords(consumer, ofSeconds(10), 2);
      var topics = stream(records.spliterator(), false).map(ConsumerRecord::topic).toList();

      assertThat(topics).containsExactlyInAnyOrder(TRAFFIC_EVENTS_TOPIC, TRAFFIC_EVENTS_DLT_TOPIC);
    }
  }

  @Test
  void
      givenValidTrafficEvent_WhenSentToTrafficEventsTopicAndServiceRejectsTheEvent_ThenTrafficEventIsFirstPassedToRetryTopicAndThenDltHandler() {
    when(trafficProcessorService.processTrafficEvent(any())).thenThrow(ServiceException.class);

    var trafficEvent = Instancio.create(KafkaTrafficEvent.class);
    kafkaTemplate.send(TRAFFIC_EVENTS_TOPIC, trafficEvent);

    try (var consumer = createTestConsumer()) {
      embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
      var records = getRecords(consumer, ofSeconds(10), 4);
      var topics = stream(records.spliterator(), false).map(ConsumerRecord::topic).toList();

      assertThat(topics)
          .containsExactlyInAnyOrder(
              TRAFFIC_EVENTS_TOPIC,
              DEDUPLICATED_TRAFFIC_EVENTS_TOPIC,
              DEDUPLICATED_TRAFFIC_EVENTS_RETRY_TOPIC,
              DEDUPLICATED_TRAFFIC_EVENTS_DLT_TOPIC);
    }
  }

  private Consumer<String, KafkaTrafficEvent> createTestConsumer() {
    var consumerProps = consumerProps(getClass().getName(), "true", embeddedKafkaBroker);
    consumerProps.put(AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(
            consumerProps,
            new StringDeserializer(),
            new JsonDeserializer<>(KafkaTrafficEvent.class))
        .createConsumer();
  }
}
