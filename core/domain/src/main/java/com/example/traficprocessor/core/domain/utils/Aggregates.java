package com.example.traficprocessor.core.domain.utils;

import static com.example.traficprocessor.core.domain.utils.Values.cast;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Stream.empty;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Aggregates {
  private Aggregates() {}

  public static <T> boolean isEmpty(Collection<T> collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T> boolean isNotEmpty(Collection<T> collection) {
    return collection != null && !collection.isEmpty();
  }

  public static int nullSafeSize(Collection<?> collection) {
    return collection == null ? 0 : collection.size();
  }

  public static <K, V> boolean isEmpty(Map<K, V> map) {
    return map == null || map.isEmpty();
  }

  public static <K, V> boolean isNotEmpty(Map<K, V> map) {
    return map != null && !map.isEmpty();
  }

  public static int nullSafeSize(Map<?, ?> map) {
    return map == null ? 0 : map.size();
  }

  public static <T> List<T> safeList(List<T> original) {
    return original == null ? List.of() : List.copyOf(original);
  }

  public static <T> List<T> mutableList(List<T> original) {
    return original == null ? new ArrayList<>() : new ArrayList<>(original);
  }

  public static <T> List<T> nonNullList(List<T> original) {
    return original == null ? List.of() : original;
  }

  @SafeVarargs
  public static <T> Stream<T> concat(Stream<? extends T> s1, Stream<? extends T>... ss) {
    return stream(ss)
        .reduce(safeStream(s1), (s, s2) -> Stream.concat(s, safeStream(s2)))
        .map(Values::cast);
  }

  public static <T> Stream<T> concatToStream(
      Collection<? extends T> c1, Collection<? extends T> c2) {
    return Stream.concat(safeStream(c1), safeStream(c2));
  }

  @SafeVarargs
  public static <T> Stream<T> concatToStream(
      Collection<? extends T> c1, Collection<? extends T>... cs) {
    return stream(cs)
        .reduce(cast(safeStream(c1)), (s, c) -> Stream.concat(s, safeStream(c)), Stream::concat);
  }

  public static <T> Stream<T> intersection(Collection<? extends T> c1, Collection<? extends T> c2) {
    if (c1 == null || c2 == null) {
      return empty();
    }

    return c1.stream().filter(c2::contains).distinct().map(Values::cast);
  }

  public static <T> Stream<T> intersection(Stream<? extends T> s1, Collection<? extends T> c2) {
    if (s1 == null || c2 == null) {
      return empty();
    }

    return s1.filter(c2::contains).distinct().map(Values::cast);
  }

  public static <T> List<T> concat(Collection<? extends T> l1, Collection<? extends T> l2) {
    return Stream.concat(safeStream(l1), safeStream(l2)).toList();
  }

  @SafeVarargs
  public static <T> List<T> concat(Collection<? extends T> l1, Collection<? extends T>... ls) {
    return concatToStream(l1, ls).toList();
  }

  public static <T> List<T> concatTo(List<T> l1, List<? extends T> l2) {
    l1.addAll(l2);
    return l1;
  }

  public static <T> List<T> append(List<? extends T> list, T value) {
    return Stream.concat(safeStream(list), Stream.of(value)).toList();
  }

  public static <T> List<T> appendTo(List<T> list, T value) {
    list.add(value);
    return list;
  }

  public static <T> List<T> prepend(T value, List<? extends T> list) {
    return Stream.concat(Stream.of(value), safeStream(list)).toList();
  }

  public static <T> List<T> prependTo(T value, List<T> list) {
    list.add(0, value);
    return list;
  }

  public static <T> T getElementSafely(List<T> list, int index) {
    if (list == null) {
      return null;
    } else {
      return index >= 0 && index < list.size() ? list.get(index) : null;
    }
  }

  public static <T> List<T> intersectionToList(
      Collection<? extends T> c1, Collection<? extends T> c2) {
    return intersection(c1, c2).collect(toList());
  }

  public static <T> Set<T> safeSet(Set<T> original) {
    return original == null ? Set.of() : Set.copyOf(original);
  }

  public static <T> Set<T> union(Collection<? extends T> s1, Collection<? extends T> s2) {
    return Stream.concat(safeStream(s1), safeStream(s2)).collect(toSet());
  }

  @SafeVarargs
  public static <T> Set<T> union(Collection<? extends T> s1, Collection<? extends T>... ss) {
    return concatToStream(s1, ss).collect(toSet());
  }

  public static <T> Set<T> unionTo(Set<T> s1, Collection<? extends T> s2) {
    s1.addAll(s2);
    return s1;
  }

  public static <T> Stream<T> unionToStream(
      Collection<? extends T> s1, Collection<? extends T> s2) {
    return Stream.concat(safeStream(s1), safeStream(s2)).distinct();
  }

  @SafeVarargs
  public static <T> Stream<T> unionToStream(
      Collection<? extends T> s1, Collection<? extends T>... ss) {
    return concatToStream(s1, ss).distinct();
  }

  public static <T> Set<T> append(Set<? extends T> set, T value) {
    return Stream.concat(safeStream(set), Stream.of(value)).collect(toSet());
  }

  public static <T> Set<T> appendTo(Set<T> set, T value) {
    set.add(value);
    return set;
  }

  public static <T> Set<? extends T> intersectionToSet(
      Collection<? extends T> c1, Collection<? extends T> c2) {
    return intersection(c1, c2).collect(toSet());
  }

  public static <T> T[] concat(T[] a1, T[] a2) {
    var copy = Arrays.copyOf(a1, a1.length + a2.length);
    System.arraycopy(a2, 0, copy, a1.length, a2.length);
    return copy;
  }

  public static <T> T[] append(T[] arr, T value) {
    var copy = Arrays.copyOf(arr, arr.length + 1);
    copy[arr.length] = value;
    return copy;
  }

  public static <K, V, M extends Map<? extends K, ? extends V>> Map<K, V> merge(M m1, M m2) {
    return Stream.concat(safeStream(m1.entrySet()), safeStream(m2.entrySet()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  public static <K, V, M extends Map<? extends K, ? extends V>> Map<K, V> merge(
      M m1, M m2, BinaryOperator<V> mergeFunction) {
    return Stream.concat(safeStream(m1.entrySet()), safeStream(m2.entrySet()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, mergeFunction));
  }

  public static <K, V> Map<K, V> mergeTo(Map<K, V> m1, Map<? extends K, ? extends V> m2) {
    m1.putAll(m2);
    return m1;
  }

  public static <T> Stream<T> safeStream(T[] values) {
    return values == null ? Stream.empty() : Arrays.stream(values);
  }

  public static <T> Stream<T> safeStream(Collection<T> collection) {
    return collection == null ? Stream.empty() : collection.stream();
  }

  public static <T> Stream<T> safeStream(Stream<T> stream) {
    return stream == null ? Stream.empty() : stream;
  }

  public static <T> Stream<T> safeStream(Iterable<T> iterable) {
    return switch (iterable) {
      case Collection<T> col -> col.stream();
      case null -> Stream.empty();
      default -> StreamSupport.stream(iterable.spliterator(), false);
    };
  }

  public static <T> Stream<T> safeStream(Iterator<T> iterator) {
    return iterator == null
        ? Stream.empty()
        : StreamSupport.stream(spliteratorUnknownSize(iterator, 0), false);
  }

  @SafeVarargs
  public static <T> Optional<T> firstNonNull(T... values) {
    return stream(values).filter(Objects::nonNull).findFirst();
  }

  @SafeVarargs
  public static <T> T firstNonNullOrElse(T defaultValue, T... values) {
    return stream(values).filter(Objects::nonNull).findFirst().orElse(defaultValue);
  }

  @SafeVarargs
  public static <T> T firstNonNullOrGet(Supplier<T> defaultValue, T... values) {
    return stream(values).filter(Objects::nonNull).findFirst().orElseGet(defaultValue);
  }

  public static <T extends UnaryOperator<T>, K> Collector<T, ?, Map<K, T>> mapFoldable(
      Function<? super T, ? extends K> keyMapper) {
    return toMap(keyMapper, identity(), UnaryOperator::apply);
  }

  public static <T extends UnaryOperator<T>, K> Collector<T, ?, Map<K, T>> mapFoldableOrdered(
      Function<? super T, ? extends K> keyMapper) {
    return toMap(keyMapper, identity(), UnaryOperator::apply, LinkedHashMap::new);
  }

  public static <T, K, U extends UnaryOperator<U>> Collector<T, ?, Map<K, U>> mapFoldable(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toMap(keyMapper, valueMapper, UnaryOperator::apply);
  }

  public static <T, K, U extends UnaryOperator<U>> Collector<T, ?, Map<K, U>> mapFoldableOrdered(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toMap(keyMapper, valueMapper, UnaryOperator::apply, LinkedHashMap::new);
  }

  public static <K, U> Collector<Tuple2<K, U>, ?, Map<K, U>> mapTuple() {
    return toMap(Tuple2::_1, Tuple2::_2);
  }

  public static <K, U> Collector<Entry<K, U>, ?, Map<K, U>> mapEntry() {
    return toMap(Entry::getKey, Entry::getValue);
  }

  public static <T, K> Collector<T, ?, Map<K, T>> mapBy(
      Function<? super T, ? extends K> keyMapper) {
    return toMap(keyMapper, identity());
  }

  public static <T, K, U> Collector<T, ?, SequencedMap<K, U>> mapOrdered(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toMap(keyMapper, valueMapper, Aggregates::uniqueKeyMerger, LinkedHashMap::new);
  }

  public static <T, K> Collector<T, ?, SequencedMap<K, T>> mapOrderedBy(
      Function<? super T, ? extends K> keyMapper, BinaryOperator<T> mergeFunction) {
    return toMap(keyMapper, identity(), mergeFunction, LinkedHashMap::new);
  }

  public static <T, K> Collector<T, ?, SequencedMap<K, T>> mapOrderedBy(
      Function<? super T, ? extends K> keyMapper) {
    return toMap(keyMapper, identity(), Aggregates::uniqueKeyMerger, LinkedHashMap::new);
  }

  private static <T> T uniqueKeyMerger(T first, T second) {
    throw new IllegalStateException(format("Duplicate key for values %s and %s", first, second));
  }

  public static <T, K, U> Collector<T, ?, SequencedMap<K, U>> mapOrderedDistinctively(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toMap(keyMapper, valueMapper, (t1, _) -> t1, LinkedHashMap::new);
  }

  public static <T, K> Collector<T, ?, SequencedMap<K, T>> mapOrderedDistinctivelyBy(
      Function<? super T, ? extends K> keyMapper) {
    return toMap(keyMapper, identity(), (t1, _) -> t1, LinkedHashMap::new);
  }

  public static <T, K> Collector<T, ?, Map<K, T>> mapBy(
      Function<? super T, ? extends K> keyMapper, BinaryOperator<T> mergeFunction) {
    return toMap(keyMapper, identity(), mergeFunction);
  }

  public static <K, U> Collector<Tuple2<K, U>, ?, Map<K, U>> mapDistinctTuple() {
    return mapDistinct(Tuple2::_1, Tuple2::_2);
  }

  public static <K, U> Collector<Entry<K, U>, ?, Map<K, U>> mapDistinctEntry() {
    return mapDistinct(Entry::getKey, Entry::getValue);
  }

  public static <T, K, U> Collector<T, ?, Map<K, U>> mapDistinct(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toMap(keyMapper, valueMapper, (t1, _) -> t1);
  }

  public static <T, K> Collector<T, ?, Map<K, T>> mapDistinctBy(
      Function<? super T, ? extends K> keyMapper) {
    return toMap(keyMapper, identity(), (t1, _) -> t1);
  }

  public static <T, K> Collector<T, ?, Map<K, T>> immutableMapBy(
      Function<? super T, ? extends K> keyMapper, BinaryOperator<T> mergeFunction) {
    return toUnmodifiableMap(keyMapper, identity(), mergeFunction);
  }

  public static <T, K, U> Collector<T, ?, Map<K, U>> immutableMapDistinct(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toUnmodifiableMap(keyMapper, valueMapper, (t1, _) -> t1);
  }

  public static <T, K> Collector<T, ?, Map<K, T>> immutableMapDistinctBy(
      Function<? super T, ? extends K> keyMapper) {
    return toUnmodifiableMap(keyMapper, identity(), (t1, _) -> t1);
  }

  public static <T, K, D> Collector<T, ?, Map<K, List<D>>> groupBy(
      Function<? super T, ? extends K> classifier, Function<? super T, ? extends D> valueMapper) {
    return groupingBy(classifier, mapping(valueMapper, toList()));
  }

  public static <T, K> Collector<T, ?, SequencedMap<K, List<T>>> groupOrderedBy(
      Function<? super T, ? extends K> classifier) {
    return groupingBy(classifier, LinkedHashMap::new, toList());
  }

  public static <T, K, D> Collector<T, ?, SequencedMap<K, List<D>>> groupOrderedBy(
      Function<? super T, ? extends K> classifier, Function<? super T, ? extends D> valueMapper) {
    return groupingBy(classifier, LinkedHashMap::new, mapping(valueMapper, toList()));
  }

  public static <T> Predicate<T> distinct() {
    return new DistinctBy<>(identity());
  }

  public static <T, R> Predicate<T> distinctBy(Function<T, R> elementMapper) {
    return new DistinctBy<>(elementMapper);
  }

  public static <T> Predicate<T> distinctIdentity() {
    return new DistinctBy<>(identity(), true);
  }

  public static <T, R> Predicate<T> distinctIdentity(Function<T, R> elementMapper) {
    return new DistinctBy<>(elementMapper, true);
  }

  public static <T, K, U, M extends Map<K, U>> Collector<T, M, M> toPermissiveMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toPermissiveMap(keyMapper, valueMapper, HashMap::new);
  }

  public static <T, K, U>
      Collector<T, SequencedMap<K, U>, SequencedMap<K, U>> toPermissiveOrderedMap(
          Function<? super T, ? extends K> keyMapper,
          Function<? super T, ? extends U> valueMapper) {
    return toPermissiveMap(keyMapper, valueMapper, LinkedHashMap::new);
  }

  public static <T, K, U, M extends Map<K, U>> Collector<T, M, M> toPermissiveMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      Supplier<Map<K, U>> supplier) {
    return new PermissiveMapCollector<>(keyMapper, valueMapper, supplier);
  }

  public static <T> BiConsumer<Iterable<T>, Consumer<T>> multiMapIterable() {
    return (ts, c) -> ts.forEach(c);
  }

  public static <F, T> BiConsumer<F, Consumer<T>> multiMapIterable(
      Function<F, Iterable<T>> iterableSupplier) {
    return (f, c) -> iterableSupplier.apply(f).forEach(c);
  }

  public static <T1, T2> Function<T1, Stream<Tuple2<T1, T2>>> flatMapZip(
      Function<T1, Stream<T2>> mapper) {
    return t1 -> mapper.apply(t1).map(t2 -> Tuple.of(t1, t2));
  }

  public static <T1, T2> Function<T1, Stream<Tuple2<T2, T1>>> flatMapZipReverse(
      Function<T1, Stream<T2>> mapper) {
    return t1 -> mapper.apply(t1).map(t2 -> Tuple.of(t2, t1));
  }

  public static <T1, T2> BiConsumer<T1, Consumer<Tuple2<T1, T2>>> multiMapZip(
      Function<T1, Collection<T2>> mapper) {
    return (t1, c) -> mapper.apply(t1).forEach(t2 -> c.accept(Tuple.of(t1, t2)));
  }

  public static <T1, T2> BiConsumer<T1, Consumer<Tuple2<T2, T1>>> multiMapZipReverse(
      Function<T1, Collection<T2>> mapper) {
    return (t1, c) -> mapper.apply(t1).forEach(t2 -> c.accept(Tuple.of(t2, t1)));
  }

  public static class DistinctBy<T, R> implements Predicate<T> {
    private final Set<R> distinctValues;
    private final Function<T, R> elementMapper;

    public static <T, R> DistinctBy<T, R> distinctBy(Function<T, R> elementMapper) {
      return new DistinctBy<>(elementMapper);
    }

    public DistinctBy(Function<T, R> elementMapper) {
      this(elementMapper, false);
    }

    public DistinctBy(Function<T, R> elementMapper, boolean identity) {
      this.elementMapper = elementMapper;
      this.distinctValues = identity ? new IdentitySet<>() : new HashSet<>();
    }

    @Override
    public boolean test(T element) {
      R distinctable = elementMapper.apply(element);
      return distinctable != null && distinctValues.add(distinctable);
    }
  }

  private static class PermissiveMapCollector<T, K, V, M extends Map<K, V>>
      implements Collector<T, M, M> {
    private final Function<? super T, ? extends K> keyMapper;
    private final Function<? super T, ? extends V> valueMapper;
    private final Supplier<Map<K, V>> supplier;

    public PermissiveMapCollector(
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends V> valueMapper,
        Supplier<Map<K, V>> supplier) {
      this.keyMapper = keyMapper;
      this.valueMapper = valueMapper;
      this.supplier = supplier;
    }

    @Override
    public Supplier<M> supplier() {
      return () -> cast(supplier.get());
    }

    @Override
    public BiConsumer<M, T> accumulator() {
      return (map, element) ->
          addToMapAndValidate(
              map, requireNonNull(keyMapper.apply(element)), valueMapper.apply(element));
    }

    private void addToMapAndValidate(M map, K key, V value) {
      if (map.putIfAbsent(key, value) != null) {
        throw new IllegalStateException("Duplicate key " + key);
      }
    }

    @Override
    public BinaryOperator<M> combiner() {
      return this::mergeAndValidate;
    }

    private M mergeAndValidate(M m1, M m2) {
      m2.entrySet().forEach(e -> addToMapAndValidate(m1, e.getKey(), e.getValue()));
      return m1;
    }

    @Override
    public Function<M, M> finisher() {
      return identity();
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
      return Set.of(Collector.Characteristics.IDENTITY_FINISH);
    }
  }
}
