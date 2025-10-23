package com.example.traficprocessor.core.domain.i18n;

import static com.example.traficprocessor.core.domain.i18n.I18nMessageConstant.of;

public interface DomainI18nInfoConstants {
  I18nMessageConstant INTERNAL_ERROR_MESSAGE = of("DOMAIN-001", "Oops something went wrong...");
  I18nMessageConstant INVALID_TIME_PERIOD_MESSAGE =
      of("DOMAIN-002", "Invalid time period [%s, %s], 'from' must be before 'to'.");
}
