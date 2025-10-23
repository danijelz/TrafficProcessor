package com.example.traficprocessor.adapter.presentation.grpc.model;

import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcRecordedTrafficEvent.Builder;
import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;

public class GrpcRecordedTrafficEventAdapter implements RecordedTrafficEvent {
  private final Builder builder = GrpcRecordedTrafficEvent.newBuilder();

  @Override
  public String getId() {
    return builder.getId();
  }

  @Override
  public void setId(String id) {
    builder.setId(id);
  }

  @Override
  public String getVehicleId() {
    return builder.getVehicleId();
  }

  @Override
  public void setVehicleId(String vehicleId) {
    builder.setVehicleId(vehicleId);
  }

  @Override
  public VehicleBrand getVehicleBrand() {
    return VehicleBrand.values()[builder.getVehicleBrand().ordinal()];
  }

  @Override
  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    builder.setVehicleBrand(GrpcVehicleBrand.forNumber(vehicleBrand.ordinal()));
  }

  @Override
  public long getTimestamp() {
    return builder.getTimestamp();
  }

  @Override
  public void setTimestamp(long timestamp) {
    builder.setTimestamp(timestamp);
  }

  public GrpcRecordedTrafficEvent build() {
    return builder.build();
  }
}
