package com.example.traficprocessor.core.model;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Instant;

public interface IdentifiableTrafficEvent {
  static String TIMESTAMP_VEHICLE_ID_DELIMITER = "_";

  String getVehicleId();

  long getTimestamp();

  default String toId() {
    return normalizeTimestamp(getTimestamp()) + TIMESTAMP_VEHICLE_ID_DELIMITER + getVehicleId();
  }

  static long normalizeTimestamp(long timestamp) {
    return Instant.ofEpochMilli(timestamp).truncatedTo(DAYS).toEpochMilli();
  }
}
