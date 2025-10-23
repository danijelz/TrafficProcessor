package com.example.traficprocessor.adapter.persistence.dynamo.aot;

import static com.example.traficprocessor.adapter.persistence.dynamo.DynamoDbEntityScanner.findDynamoDbEntities;

import java.lang.reflect.Type;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public final class DynamoDbRuntimeHintsRegistrar implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    var reflectionHints = hints.reflection();
    var entities = findDynamoDbEntities().toArray(Type[]::new);
    new BindingReflectionHintsRegistrar().registerReflectionHints(reflectionHints, entities);
  }
}
