package com.example.traficprocessor.adapter.persistence.dynamo.observability;

import static com.example.traficprocessor.adapter.persistence.dynamo.observability.DynamoDbPoitcuts.DYNAMO_DB_OPERATIONS_EXECUTION;
import static io.micrometer.observation.Observation.createNotStarted;

import io.micrometer.observation.ObservationRegistry;
import io.vavr.control.Try;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class DynamoDbOperationsTracer {
  private final ObservationRegistry observationRegistry;

  public DynamoDbOperationsTracer(ObservationRegistry observationRegistry) {
    this.observationRegistry = observationRegistry;
  }

  @Around(DYNAMO_DB_OPERATIONS_EXECUTION)
  public Object traceDynamoDbOperations(ProceedingJoinPoint joinPoint) {
    var spanName = "dynamoDbOperations.%s".formatted(joinPoint.getSignature().getName());
    var observation = createNotStarted(spanName, observationRegistry);
    return observation.observe(() -> Try.of(() -> joinPoint.proceed()).get());
  }
}
