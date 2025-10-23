package com.example.traficprocessor.adapter.persistence.jpa;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

class PersistenceJpaTestConfig {
  @Container
  private static final PostgreSQLContainer<?> CONTAINER =
      new PostgreSQLContainer<>("postgres:latest");

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgreSQLContainer() {
    return CONTAINER;
  }
}
