package com.example.traficprocessor.adapter.persistence.dynamo.i18n;

import static com.example.traficprocessor.core.domain.i18n.I18nMessageConstant.of;

import com.example.traficprocessor.core.domain.i18n.I18nMessageConstant;

public interface DynamoI18nMessageConstants {
  I18nMessageConstant INVALID_ID_MESSAGE = of("DYNAMO-001", "Invalid id: '%s'.");
  I18nMessageConstant CONCURRENT_UPDATE_MESSAGE = of("DYNAMO-002", "Concurrent update of '%s'.");
}
