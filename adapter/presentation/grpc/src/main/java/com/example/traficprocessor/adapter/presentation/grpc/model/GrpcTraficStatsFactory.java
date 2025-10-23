package com.example.traficprocessor.adapter.presentation.grpc.model;

import com.example.traficprocessor.core.domain.TraficStatsFactory;

public class GrpcTraficStatsFactory
    implements TraficStatsFactory<GrpcTrafficStatsAdapter, GrpcVehicleBrandTrafficStatsAdapter> {
  public static final GrpcTraficStatsFactory INSTANCE = new GrpcTraficStatsFactory();

  @Override
  public GrpcTrafficStatsAdapter createTrafficStats() {
    return new GrpcTrafficStatsAdapter();
  }

  @Override
  public GrpcVehicleBrandTrafficStatsAdapter createVehicleBrandTrafficStats() {
    return new GrpcVehicleBrandTrafficStatsAdapter();
  }
}
