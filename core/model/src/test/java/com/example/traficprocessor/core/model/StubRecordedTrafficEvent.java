package com.example.traficprocessor.core.model;

public class StubRecordedTrafficEvent extends StubTrafficEvent implements RecordedTrafficEvent {
  private String id;

  public StubRecordedTrafficEvent() {}

  public StubRecordedTrafficEvent(
      String id,
      int tollStationId,
      String vehicleId,
      VehicleBrand vehicleBrand,
      long timestamp) {
    this.id = id;
    super(tollStationId, vehicleId, vehicleBrand, timestamp);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }
}
