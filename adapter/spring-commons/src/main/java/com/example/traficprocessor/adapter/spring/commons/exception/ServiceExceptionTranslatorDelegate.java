package com.example.traficprocessor.adapter.spring.commons.exception;

import com.example.traficprocessor.core.domain.exception.ServiceException;

public interface ServiceExceptionTranslatorDelegate<T extends Throwable> {
  ServiceException toServiceException(T throwable);
}
