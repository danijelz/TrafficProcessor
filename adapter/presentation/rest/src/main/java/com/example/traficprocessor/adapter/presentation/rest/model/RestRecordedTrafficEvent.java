package com.example.traficprocessor.adapter.presentation.rest.model;

import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RecordedTrafficEvent", description = "RecordedTrafficEvent description")
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
  @Schema(description = "RecordedTrafficEvent.id description")
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  @Schema(description = "RecordedTrafficEvent.vehicleId description")
  public String getVehicleId() {
    return vehicleId;
  }

  @Override
  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Override
  @Schema(description = "RecordedTrafficEvent.vehicleBrand description")
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  @Override
  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Override
  @Schema(description = "RecordedTrafficEvent.timestamp description")
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
