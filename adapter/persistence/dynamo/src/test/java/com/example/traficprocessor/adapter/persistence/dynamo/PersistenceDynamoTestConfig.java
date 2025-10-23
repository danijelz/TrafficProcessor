package com.example.traficprocessor.adapter.persistence.dynamo;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class PersistenceDynamoTestConfig {
  @Container
  @SuppressWarnings("resource")
  private static final LocalStackContainer LOCAL_STACK_CONTAINER =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
          .withServices(DYNAMODB);

  @Bean
  @ServiceConnection
  LocalStackContainer localStackContainer() {
    return LOCAL_STACK_CONTAINER;
  }
}
