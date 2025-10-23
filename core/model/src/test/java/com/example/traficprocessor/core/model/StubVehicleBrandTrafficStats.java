package com.example.traficprocessor.core.model;

public class StubVehicleBrandTrafficStats implements VehicleBrandTrafficStats {
  private VehicleBrand vehicleBrand;
  private long numberOfCountedVehicles;

  public StubVehicleBrandTrafficStats() {}

  public StubVehicleBrandTrafficStats(VehicleBrand vehicleBrand, long numberOfCountedVehicles) {
    this.vehicleBrand = vehicleBrand;
    this.numberOfCountedVehicles = numberOfCountedVehicles;
  }

  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public long getNumberOfCountedVehicles() {
    return numberOfCountedVehicles;
  }

  public void setNumberOfCountedVehicles(long numberOfCountedVehicles) {
    this.numberOfCountedVehicles = numberOfCountedVehicles;
  }
}
