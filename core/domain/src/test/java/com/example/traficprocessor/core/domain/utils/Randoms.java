package com.example.traficprocessor.core.domain.utils;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Randoms {
  private Randoms() {}

  private static ThreadLocalRandom random() {
    return ThreadLocalRandom.current();
  }

  public static int randomNat() {
    return randomInt(0, Integer.MAX_VALUE);
  }

  public static int randomInt(int min, int max) {
    int lo = Math.min(min, max);
    int hi = Math.max(min, max);
    return random().nextInt(hi - lo) + lo;
  }

  public static long randomLong() {
    return random().nextLong();
  }

  public static double randomDouble() {
    return random().nextDouble();
  }

  public static String randomString() {
    return randomString(6, 10);
  }

  public static String randomString(int count) {
    return randomString(count, count + 1);
  }

  public static String randomString(int minCount, int maxCount) {
    int targetStringLength = randomInt(minCount, maxCount);
    return range(0, targetStringLength)
        .mapToObj(_ -> toChar(random().nextFloat()))
        .map(Object::toString)
        .collect(joining());
  }

  private static char toChar(float f) {
    return (char) ('a' + (int) (f * ('z' - 'a' + 1)));
  }

  public static IntStream randomRange(int minCount, int maxCount) {
    return range(0, randomInt(minCount, maxCount));
  }

  public static Stream<String> randomStrings(int minCount, int maxCount) {
    return randomRange(minCount, maxCount).mapToObj(_ -> randomString());
  }

  public static boolean randomBool() {
    return random().nextBoolean();
  }

  public static UUID randomUuid() {
    return randomUUID();
  }

  public static String randomUuidString() {
    return randomUUID().toString();
  }
}
