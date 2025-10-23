package com.example.traficprocessor.core.domain.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Lambdas {
  public static <T> Consumer<T> nopConsumer() {
    return _ -> {};
  }

  public static <T> Predicate<T> tautology() {
    return _ -> true;
  }

  public static <T> Predicate<T> contradiction() {
    return _ -> false;
  }
}
