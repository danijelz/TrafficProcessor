package com.example.traficprocessor.adapter.presentation.rest.model;

import com.example.traficprocessor.core.domain.TraficStatsFactory;

public class RestTraficStatsFactory
    implements TraficStatsFactory<RestTrafficStats, RestVehicleBrandTrafficStats> {
  public static final RestTraficStatsFactory INSTANCE = new RestTraficStatsFactory();

  @Override
  public RestTrafficStats createTrafficStats() {
    return new RestTrafficStats();
  }

  @Override
  public RestVehicleBrandTrafficStats createVehicleBrandTrafficStats() {
    return new RestVehicleBrandTrafficStats();
  }
}
