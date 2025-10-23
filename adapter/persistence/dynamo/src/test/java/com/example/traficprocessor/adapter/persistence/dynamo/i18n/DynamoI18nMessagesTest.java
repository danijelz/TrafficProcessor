package com.example.traficprocessor.adapter.persistence.dynamo.i18n;

import com.example.traficprocessor.core.domain.i18n.I18nMessagesTest;

public class DynamoI18nMessagesTest implements I18nMessagesTest {
  @Override
  public String getPropertiesBasename() {
    return "i18n/messages-dynamo";
  }

  @Override
  public Class<?> getI18nInfoConstantsClass() {
    return DynamoI18nInfoConstants.class;
  }
}
