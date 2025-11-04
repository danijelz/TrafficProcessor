package com.example.traficprocessor.adapter.presentation.rest.model;

import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RecordedTrafficEvent", description = "Identifiable instance of TrafficEvent")
public class RestRecordedTrafficEvent implements RecordedTrafficEvent {
  private String id;
  private String vehicleId;
  private VehicleBrand vehicleBrand;
  private long timestamp;

  public RestRecordedTrafficEvent() {}

  public RestRecordedTrafficEvent(
      String id, String vehicleId, VehicleBrand vehicleBrand, long timestamp) {
    this.id = id;
    this.vehicleId = vehicleId;
    this.vehicleBrand = vehicleBrand;
    this.timestamp = timestamp;
  }

  @Override
  @Schema(description = "ID of TrafficEvent")
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  @Schema(description = "ID of a vehicle as identified by tolling system.")
  public String getVehicleId() {
    return vehicleId;
  }

  @Override
  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Override
  @Schema(description = "Brand of a vehicle as identified by tolling system.")
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  @Override
  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Override
  @Schema(description = "Timestamp of event.")
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
