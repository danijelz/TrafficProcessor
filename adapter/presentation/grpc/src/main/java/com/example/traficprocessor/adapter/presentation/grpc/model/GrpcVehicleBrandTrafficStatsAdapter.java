package com.example.traficprocessor.adapter.presentation.grpc.model;

import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcVehicleBrandTrafficStats.Builder;
import com.example.traficprocessor.core.model.VehicleBrand;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;

public class GrpcVehicleBrandTrafficStatsAdapter implements VehicleBrandTrafficStats {
  private final Builder builder;

  public GrpcVehicleBrandTrafficStatsAdapter() {
    builder = GrpcVehicleBrandTrafficStats.newBuilder();
  }

  public GrpcVehicleBrandTrafficStatsAdapter(GrpcVehicleBrandTrafficStats body) {
    builder = GrpcVehicleBrandTrafficStats.newBuilder(body);
  }

  @Override
  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    builder.setVehicleBrand(GrpcVehicleBrand.forNumber(vehicleBrand.ordinal()));
  }

  @Override
  public void setNumberOfCountedVehicles(long numberOfCountedVehicles) {
    builder.setNumberOfCountedVehicles(numberOfCountedVehicles);
  }

  public GrpcVehicleBrandTrafficStats build() {
    return builder.build();
  }
}
