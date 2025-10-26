package com.example.traficprocessor.core.domain.i18n;

public class DomainI18nMessagesTest implements I18nMessagesTest {
  @Override
  public String getPropertiesBasename() {
    return "i18n/messages";
  }

  @Override
  public Class<?> getI18nInfoConstantsClass() {
    return DomainI18nMessageConstants.class;
  }
}
