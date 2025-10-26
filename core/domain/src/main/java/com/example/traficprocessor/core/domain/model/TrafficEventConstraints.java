package com.example.traficprocessor.core.domain.model;

import com.example.traficprocessor.core.model.VehicleBrand;

public interface TrafficEventConstraints {
  /// Minimal number of characters in TrafficEvent#vehicleId
  int MIN_VEHICLE_ID_LENGTH = 3;

  /// Minimal value of TrafficEvent#timestamp
  int MIN_TIMESTAMP = 0;

  static boolean isValidTrafficEvent(String vehicleId, VehicleBrand vehicleBrand, long timestamp) {
    return isValidVehicleId(vehicleId)
        && isValidVehicleBrand(vehicleBrand)
        && isValidTimestamp(timestamp);
  }

  static boolean isValidVehicleId(String vehicleId) {
    return vehicleId != null && vehicleId.length() >= MIN_VEHICLE_ID_LENGTH;
  }

  static boolean isValidVehicleBrand(VehicleBrand vehicleBrand) {
    return vehicleBrand != null;
  }

  static boolean isValidTimestamp(long timestamp) {
    return timestamp >= MIN_TIMESTAMP;
  }
}
