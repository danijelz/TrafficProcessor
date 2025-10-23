package com.example.traficprocessor.core.model;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Instant;

public class NormalizedTrafficEvent implements IdentifiableTrafficEvent {
  private String vehicleId;
  private VehicleBrand vehicleBrand;
  private long timestamp;

  public NormalizedTrafficEvent(TrafficEvent trafficEvent) {
    this(trafficEvent.getVehicleId(), trafficEvent.getVehicleBrand(), trafficEvent.getTimestamp());
  }

  public NormalizedTrafficEvent(String vehicleId, VehicleBrand vehicleBrand, long timestamp) {
    this.vehicleId = vehicleId;
    this.vehicleBrand = vehicleBrand;
    this.timestamp = normalizeTimestamp(timestamp);
  }

  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = normalizeTimestamp(timestamp);
  }

  public static long normalizeTimestamp(long timestamp) {
    return Instant.ofEpochMilli(timestamp).truncatedTo(DAYS).toEpochMilli();
  }
}
