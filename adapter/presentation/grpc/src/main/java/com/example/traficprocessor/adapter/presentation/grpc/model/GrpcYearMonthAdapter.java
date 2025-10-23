package com.example.traficprocessor.adapter.presentation.grpc.model;

import java.time.YearMonth;

public class GrpcYearMonthAdapter {
  public static GrpcYearMonth toGrpcYearMonth(YearMonth yearMonth) {
    return GrpcYearMonth.newBuilder()
        .setYear(yearMonth.getYear())
        .setMonth(yearMonth.getMonthValue())
        .build();
  }

  public static YearMonth fromGrpcYearMonth(GrpcYearMonth grpcYearMonth) {
    return YearMonth.of(grpcYearMonth.getYear(), grpcYearMonth.getMonth());
  }
}
