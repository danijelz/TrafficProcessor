package com.example.traficprocessor.core.domain;

import com.example.traficprocessor.core.model.TrafficStats;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;

public interface TraficStatsFactory<
    TS extends TrafficStats<VTS>, VTS extends VehicleBrandTrafficStats> {
  TS createTrafficStats();

  VTS createVehicleBrandTrafficStats();
}
