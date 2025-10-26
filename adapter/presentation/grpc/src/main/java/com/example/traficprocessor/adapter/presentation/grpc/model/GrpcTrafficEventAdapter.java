package com.example.traficprocessor.adapter.presentation.grpc.model;

import static com.example.traficprocessor.core.domain.model.TrafficEventConstraints.MIN_TIMESTAMP;
import static com.example.traficprocessor.core.domain.model.TrafficEventConstraints.MIN_VEHICLE_ID_LENGTH;

import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
  @NotNull
  @Size(min = MIN_VEHICLE_ID_LENGTH)
  public String getVehicleId() {
    return body.getVehicleId();
  }

  @Override
  @NotNull
  public VehicleBrand getVehicleBrand() {
    return VehicleBrand.values()[body.getVehicleBrand().ordinal()];
  }

  @Override
  @Min(MIN_TIMESTAMP)
  public long getTimestamp() {
    return body.getTimestamp();
  }
}
