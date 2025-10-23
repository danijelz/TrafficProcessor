package com.example.traficprocessor.core.model;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Instant;

public interface IdentifiableTrafficEvent {
  String getVehicleId();

  long getTimestamp();

  default String toId() {
    return normalizeTimestamp(getTimestamp()) + "_" + getVehicleId();
  }

  static long normalizeTimestamp(long timestamp) {
    return Instant.ofEpochMilli(timestamp).truncatedTo(DAYS).toEpochMilli();
  }
}
