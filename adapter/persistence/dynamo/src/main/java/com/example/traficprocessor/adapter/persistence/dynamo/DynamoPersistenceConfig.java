package com.example.traficprocessor.adapter.persistence.dynamo;

import com.example.traficprocessor.adapter.persistence.dynamo.aot.DynamoDbManagedEntities;
import com.example.traficprocessor.adapter.persistence.dynamo.aot.DynamoDbRuntimeHintsRegistrar;
import com.example.traficprocessor.adapter.persistence.dynamo.entity.CustomTableNameResolver;
import com.example.traficprocessor.adapter.persistence.dynamo.observability.DynamoDbOperationsLogger;
import com.example.traficprocessor.adapter.persistence.dynamo.observability.DynamoDbOperationsTracer;
import com.example.traficprocessor.adapter.persistence.dynamo.repository.DynamoDbSession;
import com.example.traficprocessor.adapter.persistence.dynamo.repository.DynamoTrafficEventRepository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.PropertySource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(DynamoDbRuntimeHintsRegistrar.class)
@PropertySource(value = "classpath:persistence_dynamo.properties")
public class DynamoPersistenceConfig {
  @Bean
  static DynamoDbManagedEntities dynamoDbManagedEntities() {
    return new DynamoDbManagedEntities(DynamoDbEntityScanner.findDynamoDbEntities());
  }

  @Autowired
  void initDynamoDbSession(
      DynamoDbEnhancedClient dynamoClient, DynamoDbManagedEntities managedEntities) {
    DynamoDbSession.init(dynamoClient, managedEntities.dynamoDbEntities());
  }

  @Bean
  CustomTableNameResolver customTableNameResolver() {
    return new CustomTableNameResolver();
  }

  @Bean
  DynamoTrafficEventRepository dynamoTrafficProcessorRepository(
      DynamoDbTemplate dynamoDbTemplate) {
    return new DynamoTrafficEventRepository(dynamoDbTemplate);
  }

  @Bean
  DynamoDbOperationsLogger dynamoDbOperationsLogger() {
    return new DynamoDbOperationsLogger();
  }
}

@Configuration
@ConditionalOnClass(ObservationRegistry.class)
class DynamoDbOperationsTracerConfig {
  @Bean
  DynamoDbOperationsTracer dynamoDbOperationsTracer(ObservationRegistry observationRegistry) {
    return new DynamoDbOperationsTracer(observationRegistry);
  }
}
