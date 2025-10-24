package com.example.traficprocessor.adapter.kafka;

import static com.example.traficprocessor.adapter.kafka.KafkaConstants.DEDUPLICATED_TRAFFIC_EVENTS_TOPIC;
import static com.example.traficprocessor.adapter.kafka.KafkaConstants.TRAFFIC_EVENTS_TOPIC;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomInt;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomString;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.range;
import static java.util.stream.StreamSupport.stream;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.kafka.retrytopic.RetryTopicConstants.DEFAULT_DLT_SUFFIX;
import static org.springframework.kafka.test.utils.KafkaTestUtils.consumerProps;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getRecords;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.example.traficprocessor.core.domain.TrafficProcessorService;
import com.example.traficprocessor.core.model.TrafficEvent;
import java.time.Duration;
import java.util.ArrayList;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
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
      DEDUPLICATED_TRAFFIC_EVENTS_TOPIC,
      DEDUPLICATED_TRAFFIC_EVENTS_TOPIC + DEFAULT_DLT_SUFFIX
    })
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {KafkaConfig.class, KafkaTestConfig.class})
@TestPropertySource(properties = "spring.kafka.streams.application-id=trafficProcessor")
public class TrafficEventListenerTest {
  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
  @Autowired private KafkaTemplate<String, Object> kafkaTemplate;
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
      var records = getRecords(consumer, Duration.ofSeconds(10), 2);
      var topics = stream(records.spliterator(), false).map(ConsumerRecord::topic).toList();

      assertThat(topics).containsOnly(TRAFFIC_EVENTS_TOPIC, DEDUPLICATED_TRAFFIC_EVENTS_TOPIC);
    }
  }

  @Test
  void
      givenInvalidTrafficEvent_WhenSentToTrafficEventsTopic_ThenTrafficEventIsPassedToDeduplicatedTrafficEventsDltTopic() {
    var trafficEvent =
        Instancio.of(KafkaTrafficEvent.class)
            .generate(field("timestamp"), gen -> gen.longs().max(-1l))
            .create();
    kafkaTemplate.send(TRAFFIC_EVENTS_TOPIC, trafficEvent);

    try (var consumer = createTestConsumer()) {
      embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
      var records = getRecords(consumer, Duration.ofSeconds(10), 3);
      var topics = stream(records.spliterator(), false).map(ConsumerRecord::topic).toList();

      assertThat(topics)
          .containsOnly(
              TRAFFIC_EVENTS_TOPIC,
              DEDUPLICATED_TRAFFIC_EVENTS_TOPIC,
              DEDUPLICATED_TRAFFIC_EVENTS_TOPIC + DEFAULT_DLT_SUFFIX);
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
