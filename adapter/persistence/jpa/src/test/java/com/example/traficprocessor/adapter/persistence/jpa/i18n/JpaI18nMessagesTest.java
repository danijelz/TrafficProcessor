package com.example.traficprocessor.adapter.persistence.jpa.i18n;

import com.example.traficprocessor.core.domain.i18n.I18nMessagesTest;

public class JpaI18nMessagesTest implements I18nMessagesTest {
  @Override
  public String getPropertiesBasename() {
    return "i18n/messages-jpa";
  }

  @Override
  public Class<?> getI18nInfoConstantsClass() {
    return JpaI18nMessageConstants.class;
  }
}
