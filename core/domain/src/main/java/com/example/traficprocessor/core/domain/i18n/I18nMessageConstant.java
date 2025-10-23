package com.example.traficprocessor.core.domain.i18n;

import static java.util.Arrays.stream;

import com.example.traficprocessor.core.model.I18nMessage;

public record I18nMessageConstant(String code, String content) {
  public static I18nMessageConstant of(String code, String label) {
    return new I18nMessageConstant(code, label);
  }

  public I18nMessage toMessage(Object... args) {
    var params = args == null ? null : stream(args).map(String::valueOf).toList();
    return new I18nMessage(code, content.formatted(args), params);
  }
}
