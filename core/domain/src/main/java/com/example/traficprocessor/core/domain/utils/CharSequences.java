package com.example.traficprocessor.core.domain.utils;

import static com.example.traficprocessor.core.domain.utils.Values.ifEmptyOrElseGet;

import java.util.Objects;

public class CharSequences {
  private CharSequences() {}

  public static boolean isNotBlank(final CharSequence sequence) {
    return !isBlank(sequence);
  }

  public static boolean isBlank(final CharSequence sequence) {
    if (sequence == null) {
      return true;
    }

    int length = sequence.length();
    int index = 0;

    while ((index < length) && (sequence.charAt(index) == ' ')) {
      index++;
    }

    return index == length;
  }

  public static <T extends CharSequence> T requireNotBlank(T obj) {
    if (isBlank(obj)) {
      throw new IllegalArgumentException();
    }
    return obj;
  }

  public static <T extends CharSequence> T requireNotBlank(T obj, String message) {
    if (isBlank(obj)) {
      throw new IllegalArgumentException(message);
    }
    return obj;
  }

  public static String nullSafeString(Object obj) {
    return ifEmptyOrElseGet(obj, () -> "", Objects::toString);
  }

  public static String nullSafeString(String str) {
    return str == null ? "" : str;
  }
}
