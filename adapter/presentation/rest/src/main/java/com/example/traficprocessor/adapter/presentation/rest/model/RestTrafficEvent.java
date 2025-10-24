package com.example.traficprocessor.adapter.presentation.rest.model;

import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "TrafficEvent", description = "TrafficEvent description")
public class RestTrafficEvent implements TrafficEvent {
  private int tollStationId;
  private String vehicleId;
  private VehicleBrand vehicleBrand;
  private long timestamp;

  public RestTrafficEvent() {}

  public RestTrafficEvent(
      int tollStationId, String vehicleId, VehicleBrand vehicleBrand, long timestamp) {
    this.tollStationId = tollStationId;
    this.vehicleId = vehicleId;
    this.vehicleBrand = vehicleBrand;
    this.timestamp = timestamp;
  }

  @Override
  @Schema(description = "TrafficEvent.tollStationId description")
  public int getTollStationId() {
    return tollStationId;
  }

  public void setTollStationId(int tollStationId) {
    this.tollStationId = tollStationId;
  }

  @Override
  @Size(min = 3)
  @Schema(description = "TrafficEvent.vehicleId description")
  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Override
  @NotNull
  @Schema(description = "TrafficEvent.vehicleBrand description")
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Min(0)
  @Override
  @Schema(description = "TrafficEvent.timestamp description")
  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
