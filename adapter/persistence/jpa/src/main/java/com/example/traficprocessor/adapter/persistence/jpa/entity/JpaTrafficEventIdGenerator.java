package com.example.traficprocessor.adapter.persistence.jpa.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

import com.example.traficprocessor.core.model.IdentifiableTrafficEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.EnumSet;
import java.util.Optional;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

public class JpaTrafficEventIdGenerator implements BeforeExecutionGenerator {
  @Override
  public EnumSet<EventType> getEventTypes() {
    return INSERT_ONLY;
  }

  @Override
  public Object generate(
      SharedSessionContractImplementor session,
      Object owner,
      Object currentValue,
      EventType eventType) {
    return Optional.ofNullable(owner)
        .filter(JpaTrafficEventEntity.class::isInstance)
        .map(JpaTrafficEventEntity.class::cast)
        .map(IdentifiableTrafficEvent::toId)
        .orElseThrow();
  }

  @IdGeneratorType(JpaTrafficEventIdGenerator.class)
  @Target({METHOD, FIELD})
  @Retention(RUNTIME)
  public @interface JpaTrafficEventIdGeneratorType {}
}
