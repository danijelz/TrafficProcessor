package com.example.traficprocessor.core.domain;

import static com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus.BAD_REQUST;
import static com.example.traficprocessor.core.domain.i18n.DomainI18nInfoConstants.INVALID_TIME_PERIOD_MESSAGE;

import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.domain.repository.TrafficEventRepository;
import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.TrafficStats;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import java.time.YearMonth;
import java.util.function.Supplier;

/// Service responsible for fast filtering and processing of
/// [TrafficEvents][TrafficEvent] and validating input.
public class TrafficProcessorService {
  private final LocalTraficEventCache localTraficEventCache;
  private final TrafficEventRepository trafficProcessorRepository;

  public TrafficProcessorService(
      LocalTraficEventCache localTraficEventCache,
      TrafficEventRepository trafficProcessorRepository) {
    this.localTraficEventCache = localTraficEventCache;
    this.trafficProcessorRepository = trafficProcessorRepository;
  }

  public String processTrafficEvent(TrafficEvent trafficEvent) {
    var normalizedTrafficEvent = new NormalizedTrafficEvent(trafficEvent);
    // fast check if trafficEvent is already persisted
    if (localTraficEventCache.register(normalizedTrafficEvent)) {
      trafficProcessorRepository.persistTrafficEvent(normalizedTrafficEvent);
    }
    return normalizedTrafficEvent.toId();
  }

  public <TE extends RecordedTrafficEvent> TE retrieveTrafficEvent(
      String id, Supplier<TE> recordedTrafficEventFactory) {
    return trafficProcessorRepository.retrieveTrafficEvent(id, recordedTrafficEventFactory);
  }

  public <TS extends TrafficStats<VTS>, VTS extends VehicleBrandTrafficStats>
      TS retrieveTrafficStats(
          YearMonth timeWindowFrom,
          YearMonth timeWindowTo,
          TraficStatsFactory<TS, VTS> traficStatsFactory) {
    if (!timeWindowFrom.isBefore(timeWindowTo)) {
      var message = INVALID_TIME_PERIOD_MESSAGE.toMessage(timeWindowFrom, timeWindowTo);
      throw new ServiceException(BAD_REQUST, message);
    }

    var trafficStats = traficStatsFactory.createTrafficStats();
    trafficStats.setTimeWindowFrom(timeWindowFrom);
    trafficStats.setTimeWindowTo(timeWindowTo);
    trafficStats.setVehicleBrandTrafficStats(
        trafficProcessorRepository.retrieveTrafficStats(
            timeWindowFrom, timeWindowTo, traficStatsFactory::createVehicleBrandTrafficStats));
    return trafficStats;
  }
}
