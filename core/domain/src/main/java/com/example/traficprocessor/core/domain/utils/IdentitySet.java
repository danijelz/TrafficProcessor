package com.example.traficprocessor.core.domain.utils;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

public class IdentitySet<E> implements Set<E> {
  private static final Object VALUE = new Object();

  private final IdentityHashMap<E, Object> map;

  public IdentitySet() {
    this.map = new IdentityHashMap<>();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return map.get(o) == VALUE;
  }

  @Override
  public Iterator<E> iterator() {
    return map.keySet().iterator();
  }

  @Override
  public Object[] toArray() {
    return map.keySet().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return map.keySet().toArray(a);
  }

  @Override
  public boolean add(E o) {
    return map.put(o, VALUE) == null;
  }

  @Override
  public boolean remove(Object o) {
    return map.remove(o) == VALUE;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return map.keySet().containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return c.stream().map(this::add).reduce(false, (f, s) -> f || s);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return map.keySet().removeIf(e -> !c.contains(e));
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return map.keySet().removeIf(e -> c.contains(e));
  }

  @Override
  public void clear() {
    map.clear();
  }
}
