package com.example.traficprocessor.adapter.presentation.grpc;

import static com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus.BAD_REQUST;
import static com.example.traficprocessor.core.domain.i18n.DomainI18nMessageConstants.INVALID_TRAFFIC_EVENT_MESSAGE;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcErrorHandlerAdvice;
import com.example.traficprocessor.adapter.presentation.grpc.i18n.LocaleResolverServerInterceptor;
import com.example.traficprocessor.adapter.presentation.grpc.observability.GrpcLoggingInterceptor;
import com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionAdvisor;
import com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionTranslator;
import com.example.traficprocessor.core.domain.exception.ServiceException;
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

  @ServiceExceptionAdvisor
  ServiceException translateInvalidGrpcTrafficEventException(
      InvalidGrpcTrafficEventException exception) {
    var trafficEvent = exception.getTrafficEvent();
    var vehicleId = trafficEvent.getVehicleId();
    var vehicleBrand = trafficEvent.getVehicleBrand();
    var timestamp = trafficEvent.getTimestamp();
    var message = INVALID_TRAFFIC_EVENT_MESSAGE.toMessage(vehicleId, vehicleBrand, timestamp);
    return new ServiceException(exception, BAD_REQUST, message);
  }
}
