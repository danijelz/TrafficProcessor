package com.example.traficprocessor.core.model;

import java.time.YearMonth;
import java.util.List;

public interface TrafficStats<T extends VehicleBrandTrafficStats> {
  YearMonth getTimeWindowFrom();

  void setTimeWindowFrom(YearMonth timeWindowFrom);

  YearMonth getTimeWindowTo();

  void setTimeWindowTo(YearMonth timeWindowTo);

  List<T> getVehicleBrandTrafficStats();

  void setVehicleBrandTrafficStats(List<T> vehicleBrandTrafficStats);
}
