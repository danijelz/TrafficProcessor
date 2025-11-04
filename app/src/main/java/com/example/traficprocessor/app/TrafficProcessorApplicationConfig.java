package com.example.traficprocessor.app;

import com.example.traficprocessor.adapter.persistence.dynamo.repository.DynamoTrafficEventRepository;
import com.example.traficprocessor.adapter.persistence.jpa.repository.JpaTrafficEventRepository;
import com.example.traficprocessor.app.repository.CompositeTrafficEventRepository;
import com.example.traficprocessor.core.domain.LocalTraficEventCache;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import java.util.UUID;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RegisterReflectionForBinding(
    classes = UUID[].class,
    classNames = "com.github.benmanes.caffeine.cache.SSSMSA")
public class TrafficProcessorApplicationConfig {
  @Bean
  CaffeineCacheManager caffeineCacheManager() {
    return new CaffeineCacheManager();
  }

  @Bean
  LocalTraficEventCache localTraficEventCache(
      CaffeineCacheManager caffeineCacheManager,
      @Value("${traficprocessor.event-expiration-seconds:10800}") int expirationInSeconds) {
    return new CaffeineLocalTraficEventCache(caffeineCacheManager, expirationInSeconds);
  }

  @Bean
  CompositeTrafficEventRepository compositeTrafficProcessorRepository(
      JpaTrafficEventRepository jpaRepository, DynamoTrafficEventRepository dynamoRepository) {
    return new CompositeTrafficEventRepository(jpaRepository, dynamoRepository);
  }

  @Bean
  TrafficProcessorService trafficProcessorService(
      LocalTraficEventCache localTraficEventCache,
      CompositeTrafficEventRepository trafficProcessorRepository) {
    return new TrafficProcessorService(localTraficEventCache, trafficProcessorRepository);
  }
}
