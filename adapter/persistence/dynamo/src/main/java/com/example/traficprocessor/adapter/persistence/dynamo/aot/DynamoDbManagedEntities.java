package com.example.traficprocessor.adapter.persistence.dynamo.aot;

import java.util.List;

public record DynamoDbManagedEntities(List<Class<?>> dynamoDbEntities) {}
