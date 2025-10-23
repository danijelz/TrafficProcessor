package com.example.traficprocessor.adapter.presentation.grpc.api;

import com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionTranslator;
import com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus;
import io.grpc.Status;
import io.grpc.StatusException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

public class GrpcErrorHandlerAdvice implements GrpcExceptionHandler {
  private final ServiceExceptionTranslator exceptionTranslator;

  public GrpcErrorHandlerAdvice(ServiceExceptionTranslator exceptionTranslator) {
    this.exceptionTranslator = exceptionTranslator;
  }

  @Override
  public StatusException handleException(Throwable throwable) {
    var serviceException = exceptionTranslator.handleAndLogException(throwable);
    var localizedMessage = exceptionTranslator.getLocalizedMessage(serviceException);
    var description = "Exception %s - %s".formatted(serviceException.getId(), localizedMessage);
    var grpcStatus = toGrpcStatus(serviceException.getStatus()).withDescription(description);
    return grpcStatus.asException();
  }

  private Status toGrpcStatus(ServiceExceptionStatus status) {
    return switch (status) {
      case BAD_REQUST -> Status.INVALID_ARGUMENT;
      case NOT_FOUND -> Status.NOT_FOUND;
      case CONFLICT -> Status.ALREADY_EXISTS;
      case UNATHORIZED -> Status.UNAUTHENTICATED;
      case INTERNAL_ERROR -> Status.INTERNAL;
    };
  }
}
