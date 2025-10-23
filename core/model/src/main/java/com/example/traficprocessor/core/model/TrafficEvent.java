package com.example.traficprocessor.core.model;

public interface TrafficEvent extends IdentifiableTrafficEvent {
  int getTollStationId();

  VehicleBrand getVehicleBrand();
}
