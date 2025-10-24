package com.example.traficprocessor.adapter.persistence.jpa.repository;

import static com.example.traficprocessor.adapter.persistence.jpa.i18n.JpaI18nInfoConstants.INVALID_ID_MESSAGE;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomInt;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomString;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.traficprocessor.adapter.persistence.jpa.PersistenceJpaTest;
import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.StubRecordedTrafficEvent;
import com.example.traficprocessor.core.model.StubVehicleBrandTrafficStats;
import com.example.traficprocessor.core.model.VehicleBrand;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.function.Function;
import java.util.stream.Stream;
import org.instancio.Instancio;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PersistenceJpaTest
public class JpaTrafficEventRepositoryTest {
  @Autowired private JpaTrafficEventRepository repository;

  @Test
  void
      givenUniqueTrafficEvent_WhenPersistingToRepository_ThenTrafficEventIsRetrievableFromRepository() {
    var trafficEvent = Instancio.create(NormalizedTrafficEvent.class);
    var id = repository.persistTrafficEvent(trafficEvent);
    assertThat(id).isNotNull().isEqualTo(trafficEvent.toId());

    var saved = repository.retrieveTrafficEvent(id, StubRecordedTrafficEvent::new);
    assertThat(saved)
        .isNotNull()
        .returns(id, RecordedTrafficEvent::getId)
        .returns(trafficEvent.getVehicleId(), RecordedTrafficEvent::getVehicleId)
        .returns(trafficEvent.getVehicleBrand(), RecordedTrafficEvent::getVehicleBrand)
        .returns(trafficEvent.getTimestamp(), RecordedTrafficEvent::getTimestamp);
  }

  @Test
  void
      givenNonUniqueTrafficEvent_WhenPersistingToRepository_ThenDuplicateKeyErrorIsSuppressedAndReturnedIdIsValid() {
    var trafficEvent = Instancio.create(NormalizedTrafficEvent.class);
    repository.persistTrafficEvent(trafficEvent);

    var id = repository.persistTrafficEvent(trafficEvent);
    assertThat(id).isNotNull().isEqualTo(trafficEvent.toId());

    var saved = repository.retrieveTrafficEvent(id, StubRecordedTrafficEvent::new);
    assertThat(saved).isNotNull();
  }

  @Test
  void
      givenUnknowTrafficEventId_WhenRetrieveingTrafficEventRepository_ThenIndexViolationErrorIsSuppressedIsRetrievableFromRepository() {
    var id = new DefaultRandom().alphanumeric(17);
    assertThatExceptionOfType(ServiceException.class)
        .isThrownBy(() -> repository.retrieveTrafficEvent(id, StubRecordedTrafficEvent::new))
        .returns(INVALID_ID_MESSAGE.code(), ex -> ex.getDescription().code());
  }

  @Test
  void
      givenPersistedTrafficEvents_WhenRetrieveingTrafficStats_ThenReturndVehicleBrandTrafficStatsAreGroupedAndCounted() {
    var numberOfVehicles = randomInt(5, 7);
    var brands = VehicleBrand.values();
    record VehicleInfo(VehicleBrand brand, String id) {}
    Function<VehicleBrand, Stream<VehicleInfo>> vehicleInfoFactory =
        vb ->
            range(0, numberOfVehicles)
                .mapToObj(_ -> randomString())
                .map(id -> new VehicleInfo(vb, id));
    var vehicles = stream(brands).flatMap(vehicleInfoFactory).toList();

    var numberOfDays = randomInt(5, 7);
    var now = LocalDate.now();
    var zoneId = ZoneId.of(UTC.getId());
    
    //test events
    range(0, numberOfDays)
        .mapToObj(now::minusDays)
        .map(d -> d.atStartOfDay(zoneId).toInstant().toEpochMilli())
        .flatMap(d -> vehicles.stream().map(vi -> toTrafficEvent(vi.brand, vi.id, d)))
        .forEach(repository::persistTrafficEvent);
    //additional events
    range(0, numberOfDays)
        .map(i -> i + 10)
        .mapToObj(now::plusYears)
        .map(d -> d.atStartOfDay(zoneId).toInstant().toEpochMilli())
        .flatMap(d -> vehicles.stream().map(vi -> toTrafficEvent(vi.brand, vi.id, d)))
        .forEach(repository::persistTrafficEvent);

    var result =
        repository.retrieveTrafficStats(
            YearMonth.now().minusYears(1),
            YearMonth.now().plusYears(1),
            StubVehicleBrandTrafficStats::new);

    var numberOfVehiclesPerSearchWindow = numberOfVehicles * numberOfDays;
    assertThat(result)
        .hasSize(brands.length)
        .allMatch(vts -> vts.getNumberOfCountedVehicles() == numberOfVehiclesPerSearchWindow);
  }

  private NormalizedTrafficEvent toTrafficEvent(
      VehicleBrand vehicleBrand, String vehicleId, long timestamp) {
    return new NormalizedTrafficEvent(vehicleId, vehicleBrand, timestamp);
  }
}
