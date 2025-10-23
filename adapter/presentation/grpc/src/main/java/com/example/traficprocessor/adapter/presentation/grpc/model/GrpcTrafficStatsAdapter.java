package com.example.traficprocessor.adapter.presentation.grpc.model;

import static com.example.traficprocessor.adapter.presentation.grpc.model.GrpcYearMonthAdapter.fromGrpcYearMonth;
import static com.example.traficprocessor.adapter.presentation.grpc.model.GrpcYearMonthAdapter.toGrpcYearMonth;

import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficStats.Builder;
import com.example.traficprocessor.core.model.TrafficStats;
import java.time.YearMonth;
import java.util.List;

public class GrpcTrafficStatsAdapter implements TrafficStats<GrpcVehicleBrandTrafficStatsAdapter> {
  private final Builder builder = GrpcTrafficStats.newBuilder();

  @Override
  public YearMonth getTimeWindowFrom() {
    return fromGrpcYearMonth(builder.getTimeWindowFrom());
  }

  @Override
  public void setTimeWindowFrom(YearMonth timeWindowFrom) {
    builder.setTimeWindowFrom(toGrpcYearMonth(timeWindowFrom));
  }

  @Override
  public YearMonth getTimeWindowTo() {
    return fromGrpcYearMonth(builder.getTimeWindowTo());
  }

  @Override
  public void setTimeWindowTo(YearMonth timeWindowTo) {
    builder.setTimeWindowTo(toGrpcYearMonth(timeWindowTo));
  }

  @Override
  public List<GrpcVehicleBrandTrafficStatsAdapter> getVehicleBrandTrafficStats() {
    return builder.getVehicleBrandTrafficStatsList().stream()
        .map(GrpcVehicleBrandTrafficStatsAdapter::new)
        .toList();
  }

  @Override
  public void setVehicleBrandTrafficStats(
      List<GrpcVehicleBrandTrafficStatsAdapter> vehicleBrandTrafficStats) {
    vehicleBrandTrafficStats.stream()
        .map(GrpcVehicleBrandTrafficStatsAdapter::build)
        .forEach(builder::addVehicleBrandTrafficStats);
  }

  public GrpcTrafficStats build() {
    return builder.build();
  }
}
