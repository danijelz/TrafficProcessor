package com.example.traficprocessor.adapter.spring.commons.exception;

import static com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionTranslatorScanner.scanServiceExceptionTranslators;
import static com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus.INTERNAL_ERROR;
import static com.example.traficprocessor.core.domain.i18n.DomainI18nInfoConstants.INTERNAL_ERROR_MESSAGE;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.i18n.I18nMessages;
import com.example.traficprocessor.core.domain.utils.Values;
import io.vavr.control.Try;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class ServiceExceptionTranslator implements BeanPostProcessor {
  private static final Logger LOGGER = getLogger(ServiceExceptionTranslator.class);
  private static final String REGISTRATION_MSG =
      "Registering delegate '%s' for exceptions of type '%s'.";
  private static final String DUPLICATE_REGISTRATION_MSG =
      "Duplicate delegate registration for exception type '%s'";

  private I18nMessages messages;
  private final Map<Class<?>, ServiceExceptionTranslatorDelegate<?>> delegates = new HashMap<>();

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean instanceof I18nMessages messages) {
      this.messages = messages;
    } else {
      scanServiceExceptionTranslators(beanName, bean).forEach(this::registerDelegate);
    }

    return bean;
  }

  private void registerDelegate(ServiceExceptionTranslatorDelegateInfo info) {
    var exceptionType = info.exceptionType();
    var exceptionTypeName = exceptionType.getSimpleName();

    var existingDelegate = delegates.get(exceptionType);
    if (existingDelegate != null) {
      throw new IllegalStateException(DUPLICATE_REGISTRATION_MSG.formatted(exceptionTypeName));
    }

    var delegate = info.delegate().getClass().getSimpleName();
    LOGGER.info(REGISTRATION_MSG.formatted(delegate, exceptionType.getSimpleName()));

    delegates.put(exceptionType, info.delegate());
  }

  public ServiceException handleAndLogException(Throwable throwable) {
    var serviceException = handleException(throwable);
    var id = serviceException.getId();
    var message =
        serviceException.getStatus() == INTERNAL_ERROR
            ? "Unhandled exception '%s'.".formatted(id)
            : "Service exception '%s'.".formatted(id);
    LOGGER.error(message, serviceException);
    return serviceException;
  }

  public ServiceException handleException(Throwable throwable) {
    if (throwable instanceof ServiceException serviceException) {
      return serviceException;
    }

    return getDelegate(throwable)
        .map(d -> Try.of(() -> d.toServiceException(throwable)))
        .map(t -> t.getOrNull())
        .filter(Objects::nonNull)
        .orElseGet(() -> toInternalError(throwable));
  }

  private ServiceException toInternalError(Throwable throwable) {
    return new ServiceException(throwable, INTERNAL_ERROR, INTERNAL_ERROR_MESSAGE.toMessage());
  }

  private <T extends Throwable> Optional<ServiceExceptionTranslatorDelegate<? super T>> getDelegate(
      T throwable) {
    return Stream.<Class<?>>iterate(throwable.getClass(), Class::getSuperclass)
        .takeWhile(Objects::nonNull)
        .takeWhile(Throwable.class::isAssignableFrom)
        .<ServiceExceptionTranslatorDelegate<?>>map(delegates::get)
        .filter(Objects::nonNull)
        .findFirst()
        .or(() -> ofNullable(throwable.getCause()).flatMap(this::getDelegate))
        .<ServiceExceptionTranslatorDelegate<? super T>>map(Values::cast);
  }

  public String getLocalizedMessage(ServiceException serviceException) {
    var description = serviceException.getDescription();
    return description == null || messages == null
        ? serviceException.getMessage()
        : messages.getMessage(description);
  }
}
