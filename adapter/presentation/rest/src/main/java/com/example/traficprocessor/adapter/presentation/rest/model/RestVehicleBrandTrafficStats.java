package com.example.traficprocessor.adapter.presentation.rest.model;

import com.example.traficprocessor.core.model.VehicleBrand;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import io.swagger.v3.oas.annotations.media.Schema;

public class RestVehicleBrandTrafficStats implements VehicleBrandTrafficStats {
  private VehicleBrand vehicleBrand;
  private long numberOfCountedVehicles;

  @Schema(description = "TrafficStats.timeWindowFrom description")
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  @Override
  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Schema(description = "TrafficStats.timeWindowFrom description")
  public long getNumberOfCountedVehicles() {
    return numberOfCountedVehicles;
  }

  @Override
  public void setNumberOfCountedVehicles(long numberOfCountedVehicles) {
    this.numberOfCountedVehicles = numberOfCountedVehicles;
  }
}
