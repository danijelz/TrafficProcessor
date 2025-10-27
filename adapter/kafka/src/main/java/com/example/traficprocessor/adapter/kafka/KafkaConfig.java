package com.example.traficprocessor.adapter.kafka;

import static com.example.traficprocessor.adapter.kafka.KafkaConstants.DEDUPLICATED_TRAFFIC_EVENTS_TOPIC;
import static com.example.traficprocessor.adapter.kafka.KafkaConstants.DEDUPLICATION_STORE_NAME;
import static com.example.traficprocessor.adapter.kafka.KafkaConstants.TRAFFIC_EVENTS_TOPIC;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG;
import static org.apache.kafka.streams.state.Stores.persistentWindowStore;
import static org.apache.kafka.streams.state.Stores.windowStoreBuilder;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.kafka.retrytopic.TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE;
import static org.springframework.kafka.streams.RecoveringDeserializationExceptionHandler.KSTREAM_DESERIALIZATION_RECOVERER;

import com.example.traficprocessor.adapter.kafka.observability.KafkaListenerTracer;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import io.micrometer.observation.ObservationRegistry;
import jakarta.validation.ValidationException;
import java.time.Duration;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.streams.RecoveringDeserializationExceptionHandler;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@EnableKafkaStreams
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:kafka.properties")
@RegisterReflectionForBinding(RecoveringDeserializationExceptionHandler.class)
public class KafkaConfig implements KafkaListenerConfigurer {
  private static final Logger LOGGER = getLogger(KafkaConfig.class.getName() + ".KafkaListener");

  private final TrafficProcessorService trafficProcessorService;
  private final LocalValidatorFactoryBean validatorFactoryBean;

  public KafkaConfig(
      TrafficProcessorService trafficProcessorService,
      LocalValidatorFactoryBean validatorFactoryBean) {
    this.trafficProcessorService = trafficProcessorService;
    this.validatorFactoryBean = validatorFactoryBean;
  }

  @Override
  public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
    registrar.setValidator(validatorFactoryBean);
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, KafkaTrafficEvent> listenerFactory(
      ConsumerFactory<String, KafkaTrafficEvent> consumerFactory) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, KafkaTrafficEvent>();
    factory.getContainerProperties().setObservationEnabled(true);
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }

  @Autowired
  void configureStreamRecoverer(
      KafkaStreamsConfiguration kafkaStreamsConfiguration, ProducerFactory<?, ?> producerFactory) {
    var properties = kafkaStreamsConfiguration.asProperties();
    properties.put(
        DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
        RecoveringDeserializationExceptionHandler.class);
    properties.put(KSTREAM_DESERIALIZATION_RECOVERER, createRecoverer(producerFactory));
  }

  private DeadLetterPublishingRecoverer createRecoverer(ProducerFactory<?, ?> producerFactory) {
    var template =
        new KafkaTemplate<>(
            producerFactory,
            Map.of(VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName()));
    return new DeadLetterPublishingRecoverer(template);
  }

  @Bean
  NewTopic trafficEventsTopic() {
    return TopicBuilder.name(TRAFFIC_EVENTS_TOPIC).partitions(1).replicas(1).build();
  }

  @Autowired
  void buildTrafficEventsTopology(
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

  @RetryableTopic(
      attempts = "2",
      topicSuffixingStrategy = SUFFIX_WITH_INDEX_VALUE,
      exclude = ValidationException.class)
  @KafkaListener(
      id = "trafficProcessor-${traficprocessor.kafka-listener.id:0}",
      groupId = "trafficProcessor-${traficprocessor.kafka-listener.group-id:0}",
      topics = DEDUPLICATED_TRAFFIC_EVENTS_TOPIC)
  void deduplicatedTrafficEventsListener(@Payload @Validated KafkaTrafficEvent trafficEvent) {
    LOGGER.info("Processing Kafka Message: %s".formatted(trafficEvent.getDescription()));
    trafficProcessorService.processTrafficEvent(trafficEvent);
  }

  @DltHandler
  public void handleTrafficEventsDlt(KafkaTrafficEvent trafficEvent) {
    LOGGER.info("TrafficEvent sent to DLT: %s".formatted(trafficEvent.getDescription()));
  }
}

@Configuration
@ConditionalOnClass(ObservationRegistry.class)
class KafkaListenerTracerConfig {
  @Bean
  @ConditionalOnBean(ObservationRegistry.class)
  KafkaListenerTracer kafkaListenerTracer(ObservationRegistry observationRegistry) {
    return new KafkaListenerTracer(observationRegistry);
  }
}
