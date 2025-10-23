package com.example.traficprocessor.core.domain;

import com.example.traficprocessor.core.model.NormalizedTrafficEvent;

/// Used for fast identification of processed [NormalizedTrafficEvents][NormalizedTrafficEvent]
public interface LocalTraficEventCache {
  /// returns 'true' if [trafficEvent][NormalizedTrafficEvent] is not present in cache
  default boolean register(NormalizedTrafficEvent trafficEvent) {
    return register(trafficEvent.toId());
  }

  /// returns 'true' if 'trafficEventId' is not present in cache
  boolean register(String trafficEventId);
}
