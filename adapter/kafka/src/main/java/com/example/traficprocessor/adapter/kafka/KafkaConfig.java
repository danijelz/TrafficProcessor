package com.example.traficprocessor.adapter.kafka;

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
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
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
@RegisterReflectionForBinding(LogAndContinueExceptionHandler.class)
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
        .process(
            () -> new DeduplicationProcessor(DEDUPLICATION_STORE_NAME, expirationInSeconds),
            DEDUPLICATION_STORE_NAME)
        .to(DEDUPLICATED_TRAFFIC_EVENTS_TOPIC, Produced.with(stringSerde, jsonSerde));
  }

  @KafkaListener(
      id = "trafficProcessorGrouped-${traficprocessor.kafka-listener.id:0}",
      groupId = "trafficProcessor-${traficprocessor.kafka-listener.group-id:0}",
      topics = DEDUPLICATED_TRAFFIC_EVENTS_TOPIC)
  void groupedTrafficEventsListener(KafkaTrafficEvent trafficEvent) {
    LOGGER.info("Processing Kafka Message: %s".formatted(trafficEvent.getDescription()));
    trafficProcessorService.processTrafficEvent(trafficEvent);
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
