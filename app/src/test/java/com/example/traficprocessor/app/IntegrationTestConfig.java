package com.example.traficprocessor.app;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class IntegrationTestConfig {
  @Container
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
      new PostgreSQLContainer<>("postgres:latest");

  @Container
  @SuppressWarnings("resource")
  private static final LocalStackContainer LOCAL_STACK_CONTAINER =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
          .withServices(DYNAMODB);

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      new KafkaContainer("apache/kafka-native:latest");

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgreSQLContainer() {
    return POSTGRES_CONTAINER;
  }

  @Bean
  @ServiceConnection
  LocalStackContainer localStackContainer() {
    return LOCAL_STACK_CONTAINER;
  }

  @Bean
  @ServiceConnection
  KafkaContainer kafkaContainer() {
    return KAFKA_CONTAINER;
  }
}
