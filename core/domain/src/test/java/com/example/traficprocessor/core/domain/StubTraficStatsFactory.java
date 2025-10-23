package com.example.traficprocessor.core.domain;

import com.example.traficprocessor.core.model.StubTrafficStats;
import com.example.traficprocessor.core.model.StubVehicleBrandTrafficStats;

public class StubTraficStatsFactory
    implements TraficStatsFactory<StubTrafficStats, StubVehicleBrandTrafficStats> {
  public static final StubTraficStatsFactory INSTANCE = new StubTraficStatsFactory();

  @Override
  public StubTrafficStats createTrafficStats() {
    return new StubTrafficStats();
  }

  @Override
  public StubVehicleBrandTrafficStats createVehicleBrandTrafficStats() {
    return new StubVehicleBrandTrafficStats();
  }
}
