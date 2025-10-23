package com.example.traficprocessor.adapter.persistence.dynamo.repository;

import static com.example.traficprocessor.adapter.persistence.dynamo.entity.CustomTableNameResolver.resolveTableName;
import static java.util.function.Predicate.not;
import static org.slf4j.LoggerFactory.getLogger;

import io.vavr.control.Try;
import java.util.List;
import org.slf4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

public class DynamoDbSession {
  private static final Logger LOGGER = getLogger(DynamoDbSession.class);

  private final DynamoDbEnhancedClient dynamoClient;
  private final List<Class<?>> dynamoDbEntities;

  public static void init(DynamoDbEnhancedClient dynamoClient, List<Class<?>> dynamoDbEntities) {
    new DynamoDbSession(dynamoClient, dynamoDbEntities).init();
  }

  private DynamoDbSession(
      DynamoDbEnhancedClient dynamoClient, List<Class<?>> dynamoDbEntities) {
    this.dynamoClient = dynamoClient;
    this.dynamoDbEntities = dynamoDbEntities;
  }

  private void init() {
    dynamoDbEntities.stream()
        .peek(e -> LOGGER.info("Initializing dynamo entity '%s'.".formatted(e.getSimpleName())))
        .map(ec -> dynamoClient.table(resolveTableName(ec), TableSchema.fromBean(ec)))
        .filter(not(this::tableExists))
        .peek(t -> LOGGER.info("Creating dynamo table '%s'.".formatted(t.tableName())))
        .forEach(DynamoDbTable::createTable);
  }

  private boolean tableExists(DynamoDbTable<?> table) {
    return Try.of(() -> table.describeTable())
        .map(r -> r.table().tableName())
        .map(tn -> "Skipping creation of dynamo table '%s' since it already exists.".formatted(tn))
        .peek(LOGGER::info)
        .fold(t -> !(t instanceof ResourceNotFoundException), _ -> true);
  }
}
