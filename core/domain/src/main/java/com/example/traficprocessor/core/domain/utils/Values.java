package com.example.traficprocessor.core.domain.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Values {
  private Values() {}

  public static <T> T cast(Object value) {
    @SuppressWarnings("unchecked")
    T casted = (T) value;
    return casted;
  }

  public static boolean isNotEmpty(Object value) {
    return !isEmpty(value);
  }

  public static boolean isEmpty(Object value) {
    return switch (value) {
      case null -> true;
      case CharSequence cs -> cs.length() <= 0;
      case Map<?, ?> map -> map.isEmpty();
      case Collection<?> col -> col.isEmpty();
      case Iterable<?> iter -> iter.iterator().hasNext();
      case Object obj when obj.getClass().isArray() -> isEmptyArray(obj);
      case Object _ -> false;
    };
  }

  public static boolean isEmptyArray(Object value) {
    return switch (value) {
      case null -> true;
      case long[] la -> la.length <= 0;
      case int[] ia -> ia.length <= 0;
      case short[] sa -> sa.length <= 0;
      case char[] ca -> ca.length <= 0;
      case byte[] ba -> ba.length <= 0;
      case double[] da -> da.length <= 0;
      case float[] fa -> fa.length <= 0;
      case boolean[] ba -> ba.length <= 0;
      case Object[] oa -> oa.length <= 0;
      default -> true;
    };
  }

  public static void ifEmpty(final Object value, final Runnable action) {
    Objects.requireNonNull(action);
    if (isEmpty(value)) {
      action.run();
    }
  }

  public static <T> T ifEmptyOrNull(final Object value, final Supplier<T> action) {
    Objects.requireNonNull(action);
    return isEmpty(value) ? action.get() : null;
  }

  public static void ifEmptyOrElse(
      final Object value, final Runnable action, final Runnable other) {
    Objects.requireNonNull(action);
    Objects.requireNonNull(other);
    if (isEmpty(value)) {
      action.run();
    } else {
      other.run();
    }
  }

  public static <T> T ifEmptyOrElseGet(
      final Object value, final Supplier<T> action, final Supplier<T> other) {
    Objects.requireNonNull(action);
    Objects.requireNonNull(other);
    return isEmpty(value) ? action.get() : other.get();
  }

  public static <V, T> T ifEmptyOrElseGet(
      final V value, final Supplier<T> action, final Function<V, T> other) {
    Objects.requireNonNull(action);
    Objects.requireNonNull(other);
    return isEmpty(value) ? action.get() : other.apply(value);
  }

  public static <V> V ifEmptyOrElseGet(final V value, final Supplier<V> action) {
    Objects.requireNonNull(action);
    return isEmpty(value) ? action.get() : value;
  }

  public static void ifNotEmptyDo(final Object value, final Runnable action) {
    Objects.requireNonNull(action);
    if (isNotEmpty(value)) {
      action.run();
    }
  }

  public static <T> void ifNotEmpty(final T value, final Consumer<T> action) {
    Objects.requireNonNull(action);
    if (isNotEmpty(value)) {
      action.accept(value);
    }
  }

  public static void ifNotEmptyOrElse(
      final Object value, final Runnable action, final Runnable other) {
    Objects.requireNonNull(action);
    Objects.requireNonNull(other);
    if (isEmpty(value)) {
      other.run();
    } else {
      action.run();
    }
  }

  public static <T> T ifNotEmptyOrElse(
      final Object value, final Supplier<T> action, final Supplier<T> other) {
    Objects.requireNonNull(action);
    Objects.requireNonNull(other);
    return isEmpty(value) ? other.get() : action.get();
  }

  public static <V, T> T ifNotEmptyOrElse(
      final V value, final Function<V, T> other, final Supplier<T> action) {
    Objects.requireNonNull(action);
    Objects.requireNonNull(other);
    return isNotEmpty(value) ? other.apply(value) : action.get();
  }

  public static <T> T ifNotEmptyOrGet(final T value, T other) {
    return isEmpty(value) ? other : value;
  }

  public static <T> T ifNotEmptyOrGet(final T value, Supplier<T> other) {
    return isEmpty(value) ? other.get() : value;
  }

  public static <T> T requireNotEmpty(T obj) {
    if (isEmpty(obj)) {
      throw new IllegalArgumentException();
    }
    return obj;
  }

  public static <T> T requireNotEmpty(T obj, String message) {
    if (isEmpty(obj)) {
      throw new IllegalArgumentException(message);
    }
    return obj;
  }

  public static void runIf(boolean condition, Runnable action) {
    Objects.requireNonNull(action);
    if (condition) {
      action.run();
    }
  }

  public static <T> T getOrElse(boolean condition, Supplier<T> action, Supplier<T> other) {
    Objects.requireNonNull(action);
    Objects.requireNonNull(other);
    return condition ? action.get() : other.get();
  }

  public static <T> boolean equalIdentity(T first, T second) {
    return first != null && first == second;
  }

  @SafeVarargs
  public static <T> Optional<T> firstNonNull(T... values) {
    return Arrays.stream(values).filter(Objects::nonNull).findFirst();
  }
}
