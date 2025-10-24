package com.example.traficprocessor.core.domain.repository;

import com.example.traficprocessor.core.domain.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

public interface TrafficEventRepository {
  String persistTrafficEvent(NormalizedTrafficEvent trafficEvent);

  <TE extends RecordedTrafficEvent> TE retrieveTrafficEvent(
      String id, Supplier<TE> recordedTrafficEventFactory);

  <VTS extends VehicleBrandTrafficStats> List<VTS> retrieveTrafficStats(
      YearMonth timeWindowFrom,
      YearMonth timeWindowTo,
      Supplier<VTS> vehicleBrandTrafficStatsFactory);
}
