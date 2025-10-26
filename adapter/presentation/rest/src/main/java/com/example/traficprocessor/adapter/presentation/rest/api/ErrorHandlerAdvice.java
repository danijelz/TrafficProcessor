package com.example.traficprocessor.adapter.presentation.rest.api;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionTranslator;
import com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(HIGHEST_PRECEDENCE)
public class ErrorHandlerAdvice {
  private final ServiceExceptionTranslator exceptionTranslator;

  public ErrorHandlerAdvice(ServiceExceptionTranslator exceptionTranslator) {
    this.exceptionTranslator = exceptionTranslator;
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleException(Throwable throwable) {
    var serviceException = exceptionTranslator.handleAndLogException(throwable);
    var problem =
        Problem.create()
            .withTitle("Exception ID: " + serviceException.getId())
            .withDetail(exceptionTranslator.getLocalizedMessage(serviceException));
    return new ResponseEntity<>(problem, toHttpStatus(serviceException.getStatus()));
  }

  private HttpStatus toHttpStatus(ServiceExceptionStatus status) {
    return switch (status) {
      case BAD_REQUST -> HttpStatus.BAD_REQUEST;
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case CONFLICT -> HttpStatus.CONFLICT;
      case UNATHORIZED -> HttpStatus.UNAUTHORIZED;
      case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
