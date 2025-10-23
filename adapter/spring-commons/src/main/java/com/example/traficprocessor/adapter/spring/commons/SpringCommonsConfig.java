package com.example.traficprocessor.adapter.spring.commons;

import com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionTranslator;
import com.example.traficprocessor.adapter.spring.commons.i18n.I18nMessagesBasenameProvider;
import com.example.traficprocessor.adapter.spring.commons.i18n.LocaleI18nMessages;
import com.example.traficprocessor.core.domain.i18n.I18nMessages;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration(proxyBeanMethods = false)
public class SpringCommonsConfig {
  @Bean
  MessageSource messageSource(List<I18nMessagesBasenameProvider> additionalBasenameProviders) {
    var messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setBasename("classpath:i18n/messages");
    var additionalBasenames =
        additionalBasenameProviders.stream()
            .map(I18nMessagesBasenameProvider::getBasename)
            .toArray(String[]::new);
    messageSource.addBasenames(additionalBasenames);
    return messageSource;
  }

  @Bean
  I18nMessages localeBasedTsMessages(MessageSource messageSource) {
    return new LocaleI18nMessages(messageSource);
  }

  @Bean
  static ServiceExceptionTranslator serviceExceptionTranslator() {
    return new ServiceExceptionTranslator();
  }
}
