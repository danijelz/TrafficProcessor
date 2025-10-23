package com.example.traficprocessor.adapter.presentation.rest.model;

import com.example.traficprocessor.core.model.TrafficStats;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.YearMonth;
import java.util.List;

@Schema(name = "TrafficStats", description = "TrafficStats")
public class RestTrafficStats implements TrafficStats<RestVehicleBrandTrafficStats> {
  private YearMonth timeWindowFrom;
  private YearMonth timeWindowTo;
  private List<RestVehicleBrandTrafficStats> vehicleBrandTrafficStats;

  public RestTrafficStats() {}

  public RestTrafficStats(
      YearMonth timeWindowFrom,
      YearMonth timeWindowTo,
      List<RestVehicleBrandTrafficStats> vehicleBrandTrafficStats) {
    this.timeWindowFrom = timeWindowFrom;
    this.timeWindowTo = timeWindowTo;
    this.vehicleBrandTrafficStats = vehicleBrandTrafficStats;
  }

  @Override
  @Schema(description = "TrafficStats.timeWindowFrom description")
  public YearMonth getTimeWindowFrom() {
    return timeWindowFrom;
  }

  @Override
  public void setTimeWindowFrom(YearMonth timeWindowFrom) {
    this.timeWindowFrom = timeWindowFrom;
  }

  @Override
  @Schema(description = "TrafficStats.timeWindowTo description")
  public YearMonth getTimeWindowTo() {
    return timeWindowTo;
  }

  @Override
  public void setTimeWindowTo(YearMonth timeWindowTo) {
    this.timeWindowTo = timeWindowTo;
  }

  @Override
  @Schema(description = "TrafficStats.vehicleBrandTrafficStats description")
  public List<RestVehicleBrandTrafficStats> getVehicleBrandTrafficStats() {
    return vehicleBrandTrafficStats;
  }

  @Override
  public void setVehicleBrandTrafficStats(
      List<RestVehicleBrandTrafficStats> vehicleBrandTrafficStats) {
    this.vehicleBrandTrafficStats = vehicleBrandTrafficStats;
  }
}
