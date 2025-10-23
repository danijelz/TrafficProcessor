package com.example.traficprocessor.core.domain.utils;

import static com.example.traficprocessor.core.domain.utils.Lambdas.nopConsumer;
import static java.util.Arrays.stream;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Optionals {
  static <T1, T2> Optional<Tuple2<T1, T2>> zip2(T1 _1, Optional<T2> _2) {
    return _1 == null || _2.isEmpty()
        ? Optional.empty()
        : Optional.of(Tuple.of(_1, _2.orElseThrow()));
  }

  static <T1, T2> Optional<Tuple2<T1, T2>> zip2(T1 _1, T2 _2) {
    return _1 == null || _2 == null ? Optional.empty() : Optional.of(Tuple.of(_1, _2));
  }

  static <T1, T2> Optional<Tuple2<T1, T2>> zip2(Optional<T1> _1, T2 _2) {
    return _1.isEmpty() || _2 == null
        ? Optional.empty()
        : Optional.of(Tuple.of(_1.orElseThrow(), _2));
  }

  static <T1, T2> Optional<Tuple2<T1, T2>> zip2(Optional<T1> _1, Optional<T2> _2) {
    return _1.isEmpty() || _2.isEmpty()
        ? Optional.empty()
        : Optional.of(Tuple.of(_1.orElseThrow(), _2.orElseThrow()));
  }

  static <T1, T2> Function<T1, Optional<Tuple2<T1, T2>>> zipper2(Optional<T2> _2) {
    return _1 -> zip2(_1, _2);
  }

  static <T1, T2> Function<T1, Optional<Tuple2<T1, T2>>> zipper2(Supplier<Optional<T2>> _2) {
    return _1 -> zip2(_1, _2.get());
  }

  static <T1, T2> Function<T1, Optional<Tuple2<T1, T2>>> zipper2(Function<T1, Optional<T2>> _2) {
    return _1 -> zip2(_1, _2.apply(_1));
  }

  static <T1, T2> Function<T1, Optional<Tuple2<T1, T2>>> wrapper2(Function<T1, T2> _2) {
    return _1 -> zip2(_1, _2.apply(_1));
  }

  static <T1, T2, T3> Optional<Tuple3<T1, T2, T3>> zip3(T1 _1, Optional<T2> _2, Optional<T3> _3) {
    return _1 == null || _2.isEmpty() || _3.isEmpty()
        ? Optional.empty()
        : Optional.of(Tuple.of(_1, _2.orElseThrow(), _3.orElseThrow()));
  }

  static <T1, T2, T3> Optional<Tuple3<T1, T2, T3>> zip3(T1 _1, T2 _2, Optional<T3> _3) {
    return _1 == null || _2 == null || _3.isEmpty()
        ? Optional.empty()
        : Optional.of(Tuple.of(_1, _2, _3.orElseThrow()));
  }

  static <T1, T2, T3> Optional<Tuple3<T1, T2, T3>> zip3(
      Optional<T1> _1, Optional<T2> _2, Optional<T3> _3) {
    return _1.isEmpty() || _2.isEmpty() || _3.isEmpty()
        ? Optional.empty()
        : Optional.of(Tuple.of(_1.orElseThrow(), _2.orElseThrow(), _3.orElseThrow()));
  }

  static <T1, T2, T3> Optional<Tuple3<T1, T2, T3>> zip3(Tuple2<T1, T2> _12, Optional<T3> _3) {
    return _3.isEmpty() ? Optional.empty() : Optional.of(_12.append(_3.orElseThrow()));
  }

  static <T1, T2, T3> Optional<Tuple3<T1, T2, T3>> zip3(T1 _1, Optional<Tuple2<T2, T3>> _23) {
    if (_1 == null || _23.isEmpty()) {
      return Optional.empty();
    } else {
      var t23 = _23.orElseThrow();
      return Optional.of(Tuple.of(_1, t23._1, t23._2));
    }
  }

  static <T1, T2, T3> Optional<Tuple3<T1, T2, T3>> zip3(
      Optional<Tuple2<T1, T2>> _12, Optional<T3> _3) {
    return _12.isEmpty() || _3.isEmpty()
        ? Optional.empty()
        : Optional.of(_12.orElseThrow().append(_3.orElseThrow()));
  }

  static <T1, T2, T3, T4> Optional<Tuple4<T1, T2, T3, T4>> zip4(
      T1 _1, Optional<Tuple3<T2, T3, T4>> _234) {
    if (_1 == null || _234.isEmpty()) {
      return Optional.empty();
    } else {
      var t234 = _234.orElseThrow();
      return Optional.of(Tuple.of(_1, t234._1, t234._2, t234._3));
    }
  }

  static <T1, T2, T3, T4> Optional<Tuple4<T1, T2, T3, T4>> zip4(
      Tuple3<T1, T2, T3> _123, Optional<T4> _4) {
    if (_4.isEmpty()) {
      return Optional.empty();
    } else {
      var t4 = _4.orElseThrow();
      return Optional.of(Tuple.of(_123._1, _123._2, _123._3, t4));
    }
  }

  static <T> Optional<List<T>> sequence(List<Optional<? extends T>> optionals) {
    var result = new ArrayList<T>();
    for (var optional : optionals) {
      if (optional.isEmpty()) {
        return Optional.empty();
      } else {
        result.add(optional.orElseThrow());
      }
    }

    return Optional.of(result);
  }

  static <F, T> Optional<List<T>> traverse(
      List<? extends F> values, Function<? super F, Optional<? extends T>> mapper) {
    var result = new ArrayList<T>();
    for (var value : values) {
      var mapped = mapper.apply(value);
      if (mapped.isEmpty()) {
        return Optional.empty();
      } else {
        result.add(mapped.orElseThrow());
      }
    }

    return Optional.of(result);
  }

  @SafeVarargs
  static <T> List<T> unwrap(Optional<? extends T>... optionals) {
    return stream(optionals).filter(Optional::isPresent).<T>map(Optional::orElseThrow).toList();
  }

  static <T> List<T> unwrap(List<Optional<? extends T>> optionals) {
    return optionals.stream().filter(Optional::isPresent).<T>map(Optional::orElseThrow).toList();
  }

  static <T> void ifNotPresent(Optional<T> optional, Runnable action) {
    optional.ifPresentOrElse(nopConsumer(), action);
  }
}
