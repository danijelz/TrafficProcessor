package com.example.traficprocessor.core.domain;

import static com.example.traficprocessor.core.domain.i18n.DomainI18nInfoConstants.INVALID_TIME_PERIOD_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.domain.repository.TrafficEventRepository;
import com.example.traficprocessor.core.model.StubRecordedTrafficEvent;
import com.example.traficprocessor.core.model.StubTrafficEvent;
import com.example.traficprocessor.core.model.StubVehicleBrandTrafficStats;
import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.TrafficStats;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import java.time.YearMonth;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

public class TrafficProcessorServiceTest {
  @Test
  void
      givenUncachedTrafficEventId_WhenProcessing_ThenTrafficEventIsPassedToRepositoryForPersistence() {
    var localTraficEventCache = mock(LocalTraficEventCache.class);
    when(localTraficEventCache.register((NormalizedTrafficEvent) any())).thenReturn(true);

    var trafficProcessorRepository = mock(TrafficEventRepository.class);
    when(trafficProcessorRepository.persistTrafficEvent(any()))
        .then(
            invocation -> {
              var trafficEvent = invocation.<NormalizedTrafficEvent>getArgument(0);
              return trafficEvent.toId();
            });

    var trafficProcessorService =
        new TrafficProcessorService(localTraficEventCache, trafficProcessorRepository);
    var trafficEvent = Instancio.create(StubTrafficEvent.class);
    var normalizedTrafficEvent = new NormalizedTrafficEvent(trafficEvent);
    var id = trafficProcessorService.processTrafficEvent(trafficEvent);
    assertThat(id).isEqualTo(normalizedTrafficEvent.toId());

    verify(localTraficEventCache, times(1)).register((NormalizedTrafficEvent) any());
    verify(trafficProcessorRepository, times(1)).persistTrafficEvent(any());
  }

  @Test
  void
      givenCachedTrafficEventId_WhenProcessing_ThenTrafficEventIsPassedToRepositoryForPersistence() {
    var localTraficEventCache = mock(LocalTraficEventCache.class);
    when(localTraficEventCache.register((NormalizedTrafficEvent) any())).thenReturn(false);

    var trafficProcessorRepository = mock(TrafficEventRepository.class);
    when(trafficProcessorRepository.persistTrafficEvent(any()))
        .then(
            invocation -> {
              var trafficEvent = invocation.<TrafficEvent>getArgument(0);
              return trafficEvent.toId();
            });

    var trafficProcessorService =
        new TrafficProcessorService(localTraficEventCache, trafficProcessorRepository);
    var trafficEvent = Instancio.create(StubTrafficEvent.class);
    var id = trafficProcessorService.processTrafficEvent(trafficEvent);
    var normalizedTrafficEvent = new NormalizedTrafficEvent(trafficEvent);
    assertThat(id).isEqualTo(normalizedTrafficEvent.toId());

    verify(localTraficEventCache, times(1)).register((NormalizedTrafficEvent) any());
    verify(trafficProcessorRepository, never()).persistTrafficEvent(any());
  }

  @Test
  void whenRetrieveingTrafficEventById_ThenRepositoryIsInvoked() {
    var trafficEvent = Instancio.create(StubRecordedTrafficEvent.class);
    var trafficProcessorRepository = mock(TrafficEventRepository.class);
    when(trafficProcessorRepository.retrieveTrafficEvent(any(), any())).thenReturn(trafficEvent);
    var trafficProcessorService = new TrafficProcessorService(null, trafficProcessorRepository);
    trafficProcessorService.retrieveTrafficEvent(
        trafficEvent.toId(), StubRecordedTrafficEvent::new);
    verify(trafficProcessorRepository, times(1)).retrieveTrafficEvent(any(), any());
  }

  @Test
  void
      givenValidYearMonthPeriod_WhenRetrieveingTrafficStats_ThenRepositoryIsInvokedAndResultIsValid() {
    var trafficProcessorRepository = mock(TrafficEventRepository.class);
    var vehicleBrandTrafficStats =
        List.<VehicleBrandTrafficStats>of(Instancio.create(StubVehicleBrandTrafficStats.class));
    when(trafficProcessorRepository.retrieveTrafficStats(any(), any(), any()))
        .thenReturn(vehicleBrandTrafficStats);

    var trafficProcessorService = new TrafficProcessorService(null, trafficProcessorRepository);
    var timeWindowFrom = YearMonth.now();
    var timeWindowTo = timeWindowFrom.plusYears(1);
    var stats =
        trafficProcessorService.retrieveTrafficStats(
            timeWindowFrom, timeWindowTo, StubTraficStatsFactory.INSTANCE);
    verify(trafficProcessorRepository, times(1)).retrieveTrafficStats(any(), any(), any());

    assertThat(stats)
        .returns(timeWindowFrom, TrafficStats::getTimeWindowFrom)
        .returns(timeWindowTo, TrafficStats::getTimeWindowTo)
        .returns(vehicleBrandTrafficStats, TrafficStats::getVehicleBrandTrafficStats);
  }

  @Test
  void givenInvalidYearMonthPeriod_WhenRetrieveingTrafficStats_ThenServiceExceptionIsThrown() {
    var trafficProcessorService = new TrafficProcessorService(null, null);
    var timeWindowFrom = YearMonth.now();
    var timeWindowTo = timeWindowFrom.minusYears(1);
    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(
            () ->
                trafficProcessorService.retrieveTrafficStats(
                    timeWindowFrom, timeWindowTo, StubTraficStatsFactory.INSTANCE))
        .returns(INVALID_TIME_PERIOD_MESSAGE.code(), ex -> ex.getDescription().code());
  }
}
