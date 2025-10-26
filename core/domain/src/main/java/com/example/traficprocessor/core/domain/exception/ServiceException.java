package com.example.traficprocessor.core.domain.exception;

import static com.example.traficprocessor.core.domain.i18n.DomainI18nMessageConstants.INTERNAL_ERROR_MESSAGE;
import static com.example.traficprocessor.core.domain.utils.CharSequences.isBlank;
import static java.util.UUID.randomUUID;

import com.example.traficprocessor.core.model.I18nMessage;
import java.util.UUID;

public class ServiceException extends RuntimeException {
  private final UUID id;
  private final ServiceExceptionStatus status;
  private final I18nMessage description;

  public ServiceException(ServiceExceptionStatus status, I18nMessage description) {
    super(safeMessage(description.content()));
    this.id = randomUUID();
    this.status = status;
    this.description = description;
  }

  public ServiceException(Throwable cause, ServiceExceptionStatus status, I18nMessage description) {
    super(safeMessage(description.content()), cause);
    this.id = randomUUID();
    this.status = status;
    this.description = description;
  }

  private static String safeMessage(String errorMessage) {
    return isBlank(errorMessage) ? INTERNAL_ERROR_MESSAGE.content() : errorMessage;
  }

  public UUID getId() {
    return id;
  }

  public ServiceExceptionStatus getStatus() {
    return status;
  }

  public I18nMessage getDescription() {
    return description;
  }
}
