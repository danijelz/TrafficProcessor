package com.example.traficprocessor.adapter.security.oauth.i18n;

import com.example.traficprocessor.core.domain.i18n.I18nMessagesTest;

public class SecurityOauthI18nMessagesTest implements I18nMessagesTest {
  @Override
  public String getPropertiesBasename() {
    return "i18n/messages-oauth";
  }

  @Override
  public Class<?> getI18nInfoConstantsClass() {
    return SecurityOauthI18nMessageConstants.class;
  }
}
