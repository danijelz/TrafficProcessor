package com.example.traficprocessor.core.domain;

import static com.example.traficprocessor.core.domain.i18n.DomainI18nInfoConstants.INVALID_TIME_PERIOD_MESSAGE;
import static com.example.traficprocessor.core.domain.i18n.DomainI18nInfoConstants.INVALID_TRAFFIC_EVENT_MESSAGE;
import static com.example.traficprocessor.core.domain.i18n.DomainI18nInfoConstants.INVALID_VEHICLE_ID_MESSAGE;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.domain.repository.TrafficEventRepository;
import com.example.traficprocessor.core.model.I18nMessage;
import com.example.traficprocessor.core.model.StubRecordedTrafficEvent;
import com.example.traficprocessor.core.model.StubTrafficEvent;
import com.example.traficprocessor.core.model.StubVehicleBrandTrafficStats;
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
        .then(i -> i.<NormalizedTrafficEvent>getArgument(0).toId());

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
        .then(i -> i.<NormalizedTrafficEvent>getArgument(0).toId());

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
  void givenTrafficEventWithInvalidVehicleId_WhenProcessing_ThenExceptionIsThrown() {
    var localTraficEventCache = mock(LocalTraficEventCache.class);

    var trafficProcessorRepository = mock(TrafficEventRepository.class);

    var trafficProcessorService =
        new TrafficProcessorService(localTraficEventCache, trafficProcessorRepository);

    var trafficEventWithNullvehicleId =
        Instancio.of(StubTrafficEvent.class).set(field("vehicleId"), null).create();
    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(
            () -> trafficProcessorService.processTrafficEvent(trafficEventWithNullvehicleId))
        .extracting(ServiceException::getDescription)
        .extracting(I18nMessage::code)
        .isEqualTo(INVALID_TRAFFIC_EVENT_MESSAGE.code());

    var trafficEventWithShortVehicleId =
        Instancio.of(StubTrafficEvent.class)
            .generate(field("vehicleId"), gen -> gen.string().maxLength(2))
            .create();

    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(
            () -> trafficProcessorService.processTrafficEvent(trafficEventWithShortVehicleId))
        .extracting(ServiceException::getDescription)
        .extracting(I18nMessage::code)
        .isEqualTo(INVALID_TRAFFIC_EVENT_MESSAGE.code());
  }

  @Test
  void givenTrafficEventWithInvalidVehicleBrand_WhenProcessing_ThenExceptionIsThrown() {
    var localTraficEventCache = mock(LocalTraficEventCache.class);

    var trafficProcessorRepository = mock(TrafficEventRepository.class);

    var trafficProcessorService =
        new TrafficProcessorService(localTraficEventCache, trafficProcessorRepository);

    var trafficEvent =
        Instancio.of(StubTrafficEvent.class).set(field("vehicleBrand"), null).create();
    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(() -> trafficProcessorService.processTrafficEvent(trafficEvent))
        .extracting(ServiceException::getDescription)
        .extracting(I18nMessage::code)
        .isEqualTo(INVALID_TRAFFIC_EVENT_MESSAGE.code());
  }

  @Test
  void givenTrafficEventWithInvalidTimestamp_WhenProcessing_ThenExceptionIsThrown() {
    var localTraficEventCache = mock(LocalTraficEventCache.class);

    var trafficProcessorRepository = mock(TrafficEventRepository.class);

    var trafficProcessorService =
        new TrafficProcessorService(localTraficEventCache, trafficProcessorRepository);

    var trafficEvent =
        Instancio.of(StubTrafficEvent.class)
            .generate(field("timestamp"), gen -> gen.longs().max(-1l))
            .create();
    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(() -> trafficProcessorService.processTrafficEvent(trafficEvent))
        .extracting(ServiceException::getDescription)
        .extracting(I18nMessage::code)
        .isEqualTo(INVALID_TRAFFIC_EVENT_MESSAGE.code());
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
  void whenRetrieveingTrafficEventByInvalidId_ThenServiceExceptionIsThrown() {
    var trafficProcessorRepository = mock(TrafficEventRepository.class);
    var trafficProcessorService = new TrafficProcessorService(null, trafficProcessorRepository);

    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(
            () -> trafficProcessorService.retrieveTrafficEvent(null, StubRecordedTrafficEvent::new))
        .returns(INVALID_VEHICLE_ID_MESSAGE.code(), ex -> ex.getDescription().code());
    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(
            () ->
                trafficProcessorService.retrieveTrafficEvent(
                    randomString(2), StubRecordedTrafficEvent::new))
        .returns(INVALID_VEHICLE_ID_MESSAGE.code(), ex -> ex.getDescription().code());

    verify(trafficProcessorRepository, never()).retrieveTrafficEvent(any(), any());
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

    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(
            () ->
                trafficProcessorService.retrieveTrafficStats(
                    null, timeWindowTo, StubTraficStatsFactory.INSTANCE))
        .returns(INVALID_TIME_PERIOD_MESSAGE.code(), ex -> ex.getDescription().code());

    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(
            () ->
                trafficProcessorService.retrieveTrafficStats(
                    timeWindowFrom, null, StubTraficStatsFactory.INSTANCE))
        .returns(INVALID_TIME_PERIOD_MESSAGE.code(), ex -> ex.getDescription().code());
  }
}
