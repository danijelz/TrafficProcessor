package com.example.traficprocessor.adapter.persistence.dynamo.observability;

interface DynamoDbPoitcuts {
  String DYNAMO_DB_OPERATIONS_EXECUTION =
      "execution(* io.awspring.cloud.dynamodb.DynamoDbOperations.*(..))";
}
