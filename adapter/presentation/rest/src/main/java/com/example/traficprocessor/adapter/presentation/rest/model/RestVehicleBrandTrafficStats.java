package com.example.traficprocessor.adapter.presentation.rest.model;

import com.example.traficprocessor.core.model.VehicleBrand;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "VehicleBrandTrafficStats",
    description = "Statistic of TrafficEvents by VehicleBrand.")
public class RestVehicleBrandTrafficStats implements VehicleBrandTrafficStats {
  private VehicleBrand vehicleBrand;
  private long numberOfCountedVehicles;

  @Schema(description = "Brand of a vehicle as identified by tolling system.")
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  @Override
  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Schema(description = "Nuber of counted vehicles as reported by tolling system.")
  public long getNumberOfCountedVehicles() {
    return numberOfCountedVehicles;
  }

  @Override
  public void setNumberOfCountedVehicles(long numberOfCountedVehicles) {
    this.numberOfCountedVehicles = numberOfCountedVehicles;
  }
}
