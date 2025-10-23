package com.example.traficprocessor.core.model;

import java.time.YearMonth;
import java.util.List;

public class StubTrafficStats implements TrafficStats<StubVehicleBrandTrafficStats> {
  private YearMonth timeWindowFrom;
  private YearMonth timeWindowTo;
  private List<StubVehicleBrandTrafficStats> vehicleBrandTrafficStats;

  public StubTrafficStats() {}

  public StubTrafficStats(
      YearMonth timeWindowFrom,
      YearMonth timeWindowTo,
      List<StubVehicleBrandTrafficStats> vehicleBrandTrafficStats) {
    this.timeWindowFrom = timeWindowFrom;
    this.timeWindowTo = timeWindowTo;
    this.vehicleBrandTrafficStats = vehicleBrandTrafficStats;
  }

  @Override
  public YearMonth getTimeWindowFrom() {
    return timeWindowFrom;
  }

  @Override
  public void setTimeWindowFrom(YearMonth timeWindowFrom) {
    this.timeWindowFrom = timeWindowFrom;
  }

  @Override
  public YearMonth getTimeWindowTo() {
    return timeWindowTo;
  }

  @Override
  public void setTimeWindowTo(YearMonth timeWindowTo) {
    this.timeWindowTo = timeWindowTo;
  }

  @Override
  public List<StubVehicleBrandTrafficStats> getVehicleBrandTrafficStats() {
    return vehicleBrandTrafficStats;
  }

  @Override
  public void setVehicleBrandTrafficStats(
      List<StubVehicleBrandTrafficStats> vehicleBrandTrafficStats) {
    this.vehicleBrandTrafficStats = vehicleBrandTrafficStats;
  }
}
