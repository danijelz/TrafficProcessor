package com.example.traficprocessor.adapter.spring.commons.exception;

import static com.example.traficprocessor.core.domain.utils.Values.cast;
import static java.util.stream.Stream.empty;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.aop.scope.ScopedProxyUtils.isScopedTarget;
import static org.springframework.core.MethodIntrospector.selectMethods;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import com.example.traficprocessor.core.domain.exception.ServiceException;
import io.vavr.control.Try;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

class ServiceExceptionTranslatorScanner {
  private static final Logger LOGGER = getLogger(ServiceExceptionTranslatorScanner.class);
  private static final String EXCEPTION_RESOLVING_METHODS_MSG =
      "Could not resolve methods for bean with name '%s'.";

  static Stream<ServiceExceptionTranslatorDelegateInfo> scanServiceExceptionTranslators(
      String beanName, Object bean) throws BeansException {
    if (isScopedTarget(beanName)) {
      return empty();
    }

    var targetType = ClassUtils.getUserClass(bean.getClass());
    if (ServiceExceptionTranslatorDelegate.class.isAssignableFrom(targetType)) {
      return resolveBeanDelegate(beanName, bean, targetType);
    }

    return getAnnotatedMethods(beanName, targetType).stream()
        .flatMap(m -> resolveMethodDelegate(beanName, bean, m));
  }

  private static Stream<ServiceExceptionTranslatorDelegateInfo> resolveBeanDelegate(
      String beanName, Object bean, Class<?> targetType) {
    var generic =
        ResolvableType.forClass(targetType)
            .as(ServiceExceptionTranslatorDelegate.class)
            .getGeneric();
    if (generic.getType() instanceof Class exceptionType
        && Throwable.class.isAssignableFrom(exceptionType)) {
      return Stream.of(new ServiceExceptionTranslatorDelegateInfo(cast(bean), cast(exceptionType)));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Could not resolve exception type for bean '" + beanName + "'");
    }

    return empty();
  }

  private static Set<Method> getAnnotatedMethods(String beanName, Class<?> targetType) {
    return Try.of(
            () -> selectMethods(targetType, ServiceExceptionTranslatorScanner::resolveAnnotation))
        .onFailure(t -> LOGGER.debug(EXCEPTION_RESOLVING_METHODS_MSG.formatted(beanName), t))
        .map(Map::keySet)
        .getOrElse(Set::of);
  }

  private static ServiceExceptionAdvisor resolveAnnotation(Method method) {
    return findMergedAnnotation(method, ServiceExceptionAdvisor.class);
  }

  private static Stream<ServiceExceptionTranslatorDelegateInfo> resolveMethodDelegate(
      String beanName, Object bean, Method method) {
    var returnType = method.getReturnType();
    if (!ServiceException.class.isAssignableFrom(returnType)) {
      return empty();
    }

    var parameters = method.getParameters();
    if (parameters.length != 1) {
      return empty();
    }

    var exceptionType = parameters[0].getType();
    if (!Throwable.class.isAssignableFrom(exceptionType)) {
      return empty();
    }

    var delegate = new AnnotationServiceExceptionTranslatorDelegate<>(beanName, method, bean);
    return Stream.of(new ServiceExceptionTranslatorDelegateInfo(delegate, cast(exceptionType)));
  }
}
