package com.example.traficprocessor.adapter.persistence.dynamo.entity;

import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;

public class CustomTableNameResolver implements DynamoDbTableNameResolver {
  public static <T> String resolveTableName(Class<T> clazz) {
    return clazz.getAnnotation(TableName.class).value();
  }

  @Override
  public <T> String resolve(Class<T> clazz) {
    return resolveTableName(clazz);
  }
}
