package com.example.traficprocessor.adapter.presentation.grpc;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcErrorHandlerAdvice;
import com.example.traficprocessor.adapter.presentation.grpc.i18n.LocaleResolverServerInterceptor;
import com.example.traficprocessor.adapter.presentation.grpc.observability.GrpcLoggingInterceptor;
import com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionTranslator;
import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.server.GlobalServerInterceptor;

@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:presentation_grpc.properties")
public class GrpcPresentationConfig {
  @Bean
  @Order(HIGHEST_PRECEDENCE)
  @GlobalServerInterceptor
  ServerInterceptor grpcLoggingInterceptor() {
    return new GrpcLoggingInterceptor();
  }

  @Bean
  @Order(HIGHEST_PRECEDENCE - 1)
  @GlobalServerInterceptor
  LocaleResolverServerInterceptor localeResolverServerInterceptor() {
    return new LocaleResolverServerInterceptor();
  }

  @Bean
  GrpcErrorHandlerAdvice grpcErrorHandlerAdvice(ServiceExceptionTranslator exceptionTranslator) {
    return new GrpcErrorHandlerAdvice(exceptionTranslator);
  }
}
