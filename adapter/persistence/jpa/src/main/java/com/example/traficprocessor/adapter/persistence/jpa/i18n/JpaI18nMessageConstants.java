package com.example.traficprocessor.adapter.persistence.jpa.i18n;

import static com.example.traficprocessor.core.domain.i18n.I18nMessageConstant.of;

import com.example.traficprocessor.core.domain.i18n.I18nMessageConstant;

public interface JpaI18nMessageConstants {
  I18nMessageConstant INVALID_ID_MESSAGE = of("JPA-001", "Invalid id: '%s'.");
}
