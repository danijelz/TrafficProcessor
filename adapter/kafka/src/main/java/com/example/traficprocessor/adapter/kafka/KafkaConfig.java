package com.example.traficprocessor.adapter.kafka;

import static io.vavr.control.Try.withResources;
import static org.apache.kafka.streams.state.Stores.persistentWindowStore;
import static org.apache.kafka.streams.state.Stores.windowStoreBuilder;
import static org.slf4j.LoggerFactory.getLogger;

import com.example.traficprocessor.adapter.kafka.observability.KafkaListenerTracer;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import io.micrometer.observation.ObservationRegistry;
import java.time.Duration;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;

@EnableKafkaStreams
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:kafka.properties")
public class KafkaConfig {
  private static final Logger LOGGER = getLogger(KafkaConfig.class.getName() + ".KafkaListener");
  private static final String DEDUPLICATED_TRAFFIC_EVENTS_TOPIC = "deduplicatedTrafficEvents";
  private static final String DEDUPLICATION_STORE_NAME = "trafficEventDeduplicationStore";
  private static final String TRAFFIC_EVENTS_TOPIC = "trafficEvents";

  private final TrafficProcessorService trafficProcessorService;

  public KafkaConfig(TrafficProcessorService trafficProcessorService) {
    this.trafficProcessorService = trafficProcessorService;
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, KafkaTrafficEvent> listenerFactory(
      ConsumerFactory<String, KafkaTrafficEvent> consumerFactory) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, KafkaTrafficEvent>();
    factory.getContainerProperties().setObservationEnabled(true);
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }

  @Bean
  NewTopic trafficEventsTopic() {
    return TopicBuilder.name(TRAFFIC_EVENTS_TOPIC).partitions(1).replicas(1).build();
  }

  @Autowired
  void buildTrafficEventsPipeline(
      StreamsBuilder streamsBuilder,
      @Value("${traficprocessor.event-expiration-seconds:10800}") int expirationInSeconds) {
    var stringSerde = Serdes.String();
    var jsonSerde = new JsonSerde<>(KafkaTrafficEvent.class);

    var windowSize = Duration.ofSeconds(expirationInSeconds);
    var dedupStoreBuilder =
        windowStoreBuilder(
            persistentWindowStore(DEDUPLICATION_STORE_NAME, windowSize, windowSize, false),
            stringSerde,
            Serdes.Long());
    streamsBuilder.addStateStore(dedupStoreBuilder);

    streamsBuilder.stream(TRAFFIC_EVENTS_TOPIC, Consumed.with(stringSerde, jsonSerde))
        .selectKey((_, e) -> e.toId())
        .process(() -> new DeduplicationProcessor(expirationInSeconds), DEDUPLICATION_STORE_NAME)
        .to(DEDUPLICATED_TRAFFIC_EVENTS_TOPIC, Produced.with(stringSerde, jsonSerde));
  }

  @KafkaListener(
      id = "trafficProcessorGrouped-${traficprocessor.kafka-listener.id:0}",
      groupId = "trafficProcessor-${traficprocessor.kafka-listener.group-id:0}",
      topics = DEDUPLICATED_TRAFFIC_EVENTS_TOPIC)
  void groupedTrafficEventsListener(KafkaTrafficEvent trafficEvent) {
    // TODO retry
    LOGGER.info("Processing Kafka Message: %s".formatted(trafficEvent.getDescription()));
    trafficProcessorService.processTrafficEvent(trafficEvent);
  }

  private static class DeduplicationProcessor
      implements Processor<String, KafkaTrafficEvent, String, KafkaTrafficEvent> {
    private final long searchWindowMs;

    private ProcessorContext<String, KafkaTrafficEvent> context;
    private WindowStore<String, Long> eventIdStore;

    DeduplicationProcessor(long expirationInSeconds) {
      searchWindowMs = (expirationInSeconds * 1000 / 2) + 1;
    }

    @Override
    public void init(ProcessorContext<String, KafkaTrafficEvent> context) {
      this.context = context;
      eventIdStore = context.getStateStore(DEDUPLICATION_STORE_NAME);
    }

    @Override
    public void process(Record<String, KafkaTrafficEvent> record) {
      var eventId = record.key();
      var trafficEvent = record.value();

      if (eventId == null) {
        context.forward(record);
      }

      var timestamp = context.currentStreamTimeMs();
      var duplicate = isDuplicate(eventId, timestamp);
      eventIdStore.put(eventId, timestamp, timestamp);
      if (!duplicate) {
        context.forward(record.withValue(trafficEvent));
      }
    }

    private boolean isDuplicate(String eventId, long timestamp) {
      var timeFrom = timestamp - searchWindowMs;
      var timeTo = timestamp + searchWindowMs;
      return withResources(() -> eventIdStore.fetch(eventId, timeFrom, timeTo))
          .of(WindowStoreIterator::hasNext)
          .getOrElse(false);
    }
  }
}

@Configuration
@ConditionalOnClass(ObservationRegistry.class)
class KafkaListenerTracerConfig {
  @Bean
  KafkaListenerTracer kafkaListenerTracer(ObservationRegistry observationRegistry) {
    return new KafkaListenerTracer(observationRegistry);
  }
}
