package com.example.traficprocessor.adapter.spring.commons.exception;

import static org.slf4j.LoggerFactory.getLogger;

import com.example.traficprocessor.core.domain.exception.ServiceException;
import io.vavr.control.Try;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

public class AnnotationServiceExceptionTranslatorDelegate<T extends Throwable>
    implements ServiceExceptionTranslatorDelegate<T> {
  private static final Logger LOGGER =
      getLogger(AnnotationServiceExceptionTranslatorDelegate.class);
  private static final String ERROR_MSG =
      "Error while translating exception '%s' with method '%s' on bean '%s'";

  private final String beanName;
  private final Object bean;
  private final Method method;

  public AnnotationServiceExceptionTranslatorDelegate(String beanName, Method method, Object bean) {
    this.beanName = beanName;
    this.bean = bean;
    this.method = method;

    ReflectionUtils.makeAccessible(this.method);
  }

  @Override
  public ServiceException toServiceException(T throwable) {
    return Try.of(() -> method.invoke(bean, throwable))
        .map(ServiceException.class::cast)
        .onFailure(te -> logTranslationException(throwable, te))
        .get();
  }

  private void logTranslationException(Throwable throwable, Throwable translationException) {
    var throwableType = throwable.getClass().getSimpleName();
    var methodName = method.getName();
    LOGGER.error(ERROR_MSG.formatted(throwableType, methodName, beanName), translationException);
  }
}
