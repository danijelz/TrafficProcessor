package com.example.traficprocessor.adapter.persistence.jpa;

import com.example.traficprocessor.adapter.persistence.jpa.repository.JpaTrafficEventEntityRepository;
import com.example.traficprocessor.adapter.persistence.jpa.repository.JpaTrafficEventRepository;
import com.example.traficprocessor.adapter.spring.commons.i18n.I18nMessagesBasenameProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:persistence_jpa.properties")
@EntityScan("com.example.traficprocessor.adapter.persistence.jpa.entity")
@EnableJpaRepositories("com.example.traficprocessor.adapter.persistence.jpa.repository")
public class JpaPersistenceConfig {
  @Bean
  JpaTrafficEventRepository jpaTrafficEventRepository(
      JpaTrafficEventEntityRepository entityRepository) {
    return new JpaTrafficEventRepository(entityRepository);
  }

  @Bean
  I18nMessagesBasenameProvider jpaI18nMessagesBasenameProvider() {
    return () -> "classpath:i18n/messages-jpa";
  }
}
