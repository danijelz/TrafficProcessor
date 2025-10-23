package com.example.traficprocessor.adapter.presentation.grpc.model;

import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;

public class GrpcTrafficEventAdapter implements TrafficEvent {
  private final GrpcTrafficEvent body;

  public GrpcTrafficEventAdapter(GrpcTrafficEvent body) {
    this.body = body;
  }

  @Override
  public int getTollStationId() {
    return body.getTollStationId();
  }

  @Override
  public String getVehicleId() {
    return body.getVehicleId();
  }

  @Override
  public VehicleBrand getVehicleBrand() {
    return VehicleBrand.values()[body.getVehicleBrand().ordinal()];
  }

  @Override
  public long getTimestamp() {
    return body.getTimestamp();
  }
}
