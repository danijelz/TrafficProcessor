package com.example.traficprocessor.adapter.persistence.dynamo.observability;

import static com.example.traficprocessor.adapter.persistence.dynamo.observability.DynamoDbPoitcuts.DYNAMO_DB_OPERATIONS_EXECUTION;
import static org.slf4j.LoggerFactory.getLogger;

import io.awspring.cloud.dynamodb.DynamoDbOperations;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;

@Aspect
public class DynamoDbOperationsLogger {
  private static final Logger LOGGER = getLogger(DynamoDbOperations.class);

  @Before(DYNAMO_DB_OPERATIONS_EXECUTION)
  public void logDynamoDbOperations(JoinPoint joinPoint) {
    LOGGER.info("Executing operation '%s'".formatted(joinPoint.getSignature().getName()));
  }
}
