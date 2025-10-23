package com.example.traficprocessor.core.domain.i18n;

import static java.util.Optional.ofNullable;

import com.example.traficprocessor.core.model.I18nMessage;
import java.util.Optional;

public interface I18nMessages {
  Optional<String> getMessageTemplate(String code);

  default String getMessage(I18nMessage message) {
    var parameters =
        ofNullable(message.parameters()).map(p -> p.toArray(Object[]::new)).orElse(new Object[0]);
    return getMessageTemplate(message.code())
        .map(t -> t.formatted(parameters))
        .orElse(message.content());
  }
}
