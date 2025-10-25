package com.example.traficprocessor.adapter.kafka;

import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class KafkaTrafficEvent implements TrafficEvent {
  private int tollStationId;
  private String vehicleId;
  private VehicleBrand vehicleBrand;
  private long timestamp;

  public KafkaTrafficEvent() {}

  public KafkaTrafficEvent(
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
  @NotNull
  @Size(min = 3)
  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Override
  @NotNull
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Min(0)
  @Override
  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getDescription() {
    return "TrafficEvent [tollStationId=%s, vehicleId=%s, vehicleBrand=%s, timestamp=%s]"
        .formatted(tollStationId, vehicleId, vehicleBrand, timestamp);
  }
}
