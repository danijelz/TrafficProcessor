package com.example.traficprocessor.adapter.presentation.grpc.model;

import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficEvent.Builder;
import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;

public class GrpcTrafficEventBuilderAdapter implements TrafficEvent {
  private final Builder builder = GrpcTrafficEvent.newBuilder();

  @Override
  public int getTollStationId() {
    return builder.getTollStationId();
  }

  public void setTollStationId(int tollStationId) {
    builder.setTollStationId(tollStationId);
  }

  @Override
  public String getVehicleId() {
    return builder.getVehicleId();
  }

  public void setVehicleId(String vehicleId) {
    builder.setVehicleId(vehicleId);
  }

  @Override
  public VehicleBrand getVehicleBrand() {
    return VehicleBrand.values()[builder.getVehicleBrand().ordinal()];
  }

  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    builder.setVehicleBrand(GrpcVehicleBrand.forNumber(vehicleBrand.ordinal()));
  }

  @Override
  public long getTimestamp() {
    return builder.getTimestamp();
  }

  public void setTimestamp(long timestamp) {
    builder.setTimestamp(timestamp);
  }

  public GrpcTrafficEvent build() {
    return builder.build();
  }
}
