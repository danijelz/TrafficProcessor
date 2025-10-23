package com.example.traficprocessor.core.domain.i18n;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;

import io.vavr.control.Try;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public interface I18nMessagesTest {
  String MISSING_TRANSLATION_MSG = "Missing %s translation for code '%s' in properties '%s'";
  String FORMAT_ARGS_MISMATCH_MSG =
      "Mismatched format arguments for %s translation for code '%s' in properties '%s'";

  String getPropertiesBasename();

  Class<?> getI18nInfoConstantsClass();

  default List<String> getSupportedLocales() {
    return List.of("sl");
  }

  private static Properties loadI18nProperties(String propertiesBasename, String locale)
      throws IOException {
    var properties = new Properties();
    var path = "src/main/resources/" + propertiesBasename + "_" + locale + ".properties";
    try (var input = new FileInputStream(path)) {
      properties.load(input);
    }

    return properties;
  }

  @Test
  default void validateMessages() {
    getSupportedLocales().forEach(l -> validateMessages(l));
  }

  private void validateMessages(String locale) {
    var propertiesBasename = getPropertiesBasename();
    var i18nInfoConstantsClass = getI18nInfoConstantsClass();
    var properties = Try.of(() -> loadI18nProperties(propertiesBasename, locale)).get();
    new MessageValidator(propertiesBasename, locale, i18nInfoConstantsClass, properties).validate();
  }

  class MessageValidator {
    private final String propertiesBasename;
    private final String locale;
    private final Class<?> i18nInfoConstantsClass;
    private final Properties properties;
    private final SoftAssertions assertions = new SoftAssertions();

    private MessageValidator(
        String propertiesBasename,
        String locale,
        Class<?> i18nInfoConstantsClass,
        Properties properties) {
      this.propertiesBasename = propertiesBasename;
      this.locale = locale;
      this.i18nInfoConstantsClass = i18nInfoConstantsClass;
      this.properties = properties;
    }

    private void validate() {
      stream(i18nInfoConstantsClass.getFields())
          .filter(f -> isStatic(f.getModifiers()))
          .filter(f -> I18nMessageConstant.class.equals(f.getType()))
          .map(f -> Try.of(() -> f.get(null)).get())
          .map(I18nMessageConstant.class::cast)
          .forEach(this::validateMessageConstant);

      assertions.assertAll();
    }

    private void validateMessageConstant(I18nMessageConstant messageConstant) {
      var code = messageConstant.code();
      var messageKey = "msg." + code;
      var translationPresent = properties.containsKey(messageKey);
      assertions
          .assertThat(translationPresent)
          .as(MISSING_TRANSLATION_MSG, locale, code, propertiesBasename)
          .isTrue();

      if (!translationPresent) {
        return;
      }

      var content = messageConstant.content();
      var numberOfOriginalFormatArguments = getNumberOfFormatArguments(content);
      var propertyValue = properties.getProperty(messageKey);
      var numberOfTranslationFormatArguments = getNumberOfFormatArguments(propertyValue);
      assertions
          .assertThat(numberOfOriginalFormatArguments)
          .as(FORMAT_ARGS_MISMATCH_MSG, locale, code, propertiesBasename)
          .isEqualTo(numberOfTranslationFormatArguments);
    }

    private int getNumberOfFormatArguments(String message) {
      return message == null ? 0 : (int) message.chars().filter(c -> '%' == c).count();
    }
  }
}
