package com.example.traficprocessor.core.model;

public interface RecordedTrafficEvent {
  String getId();

  void setId(String id);

  void setVehicleId(String vehicleId);

  String getVehicleId();

  void setVehicleBrand(VehicleBrand vehicleBrand);

  VehicleBrand getVehicleBrand();

  void setTimestamp(long timestamp);

  long getTimestamp();
}
