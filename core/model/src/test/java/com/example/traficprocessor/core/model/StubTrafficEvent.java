package com.example.traficprocessor.core.model;

public class StubTrafficEvent implements TrafficEvent {
  private int tollStationId;
  private String vehicleId;
  private VehicleBrand vehicleBrand;
  private long timestamp;

  public StubTrafficEvent() {}

  public StubTrafficEvent(
      int tollStationId, String vehicleId, VehicleBrand vehicleBrand, long timestamp) {
    this.tollStationId = tollStationId;
    this.vehicleId = vehicleId;
    this.vehicleBrand = vehicleBrand;
    this.timestamp = timestamp;
  }

  @Override
  public int getTollStationId() {
    return tollStationId;
  }

  public void setTollStationId(int tollStationId) {
    this.tollStationId = tollStationId;
  }

  @Override
  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Override
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
