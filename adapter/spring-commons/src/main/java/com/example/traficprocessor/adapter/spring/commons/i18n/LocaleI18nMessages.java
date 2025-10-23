package com.example.traficprocessor.adapter.spring.commons.i18n;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import com.example.traficprocessor.core.domain.i18n.I18nMessages;
import io.vavr.control.Try;
import java.util.Optional;
import org.springframework.context.MessageSource;

public class LocaleI18nMessages implements I18nMessages {
  private final MessageSource messageSource;

  public LocaleI18nMessages(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public Optional<String> getMessageTemplate(String code) {
    var key = "msg." + code;
    return Try.of(() -> resolve(key)).toJavaOptional();
  }

  private String resolve(String key) {
    return messageSource.getMessage(key, null, getLocale());
  }
}
