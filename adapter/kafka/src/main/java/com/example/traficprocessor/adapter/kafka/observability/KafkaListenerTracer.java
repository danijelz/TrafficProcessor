package com.example.traficprocessor.adapter.kafka.observability;

import static io.micrometer.observation.Observation.createNotStarted;

import io.micrometer.observation.ObservationRegistry;
import io.vavr.control.Try;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class KafkaListenerTracer {
  private static final String KAFKA_TRACING_POINTCUT =
      """
          execution(* com.example.traficprocessor.adapter.kafka.KafkaConfig.*(..)) && \
          @annotation(org.springframework.kafka.annotation.KafkaListener)""";
  private final ObservationRegistry observationRegistry;

  public KafkaListenerTracer(ObservationRegistry observationRegistry) {
    this.observationRegistry = observationRegistry;
  }

  @Around(KAFKA_TRACING_POINTCUT)
  public Object traceDynamoDbOperations(ProceedingJoinPoint joinPoint) {
    var spanName = "kafkaListener.%s".formatted(joinPoint.getSignature().getName());
    var observation = createNotStarted(spanName, observationRegistry);
    return observation.observe(() -> Try.of(() -> joinPoint.proceed()).get());
  }
}
