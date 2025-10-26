package com.example.traficprocessor.adapter.persistence.jpa.repository;

import static com.example.traficprocessor.adapter.persistence.jpa.i18n.JpaI18nMessageConstants.INVALID_ID_MESSAGE;
import static com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus.NOT_FOUND;
import static java.time.ZoneOffset.UTC;

import com.example.traficprocessor.adapter.persistence.jpa.entity.JpaTrafficEventEntity;
import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.domain.repository.TrafficEventRepository;
import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import io.vavr.control.Try;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaTrafficEventRepository implements TrafficEventRepository {
  private static final ZoneId UTC_ZONE_ID = ZoneId.of(UTC.getId());

  private final JpaTrafficEventEntityRepository entityRepository;

  public JpaTrafficEventRepository(JpaTrafficEventEntityRepository entityRepository) {
    this.entityRepository = entityRepository;
  }

  @Override
  @Transactional
  public String persistTrafficEvent(NormalizedTrafficEvent trafficEvent) {
    // Utilize database to ensure uniqueness via primary key
    return Try.of(() -> persistTrafficEventIgnoringDuplicateKey(trafficEvent))
        .recover(DuplicateKeyException.class, trafficEvent.toId())
        .get();
  }

  private String persistTrafficEventIgnoringDuplicateKey(NormalizedTrafficEvent trafficEvent) {
    var entity = new JpaTrafficEventEntity();
    entity.setVehicleId(trafficEvent.getVehicleId());
    entity.setVehicleBrand(trafficEvent.getVehicleBrand());
    entity.setTimestamp(trafficEvent.getTimestamp());
    entityRepository.save(entity);
    entityRepository.flush();
    return entity.getId();
  }

  @Override
  public <TE extends RecordedTrafficEvent> TE retrieveTrafficEvent(
      String id, Supplier<TE> trafficEventFactory) {
    return entityRepository
        .findById(id)
        .map(tee -> toRecordedTrafficEvent(id, tee, trafficEventFactory))
        .orElseThrow(() -> new ServiceException(NOT_FOUND, INVALID_ID_MESSAGE.toMessage(id)));
  }

  private <TE extends RecordedTrafficEvent> TE toRecordedTrafficEvent(
      String id, JpaTrafficEventEntity entity, Supplier<TE> recordedTrafficEventFactory) {
    var recordedTrafficEvent = recordedTrafficEventFactory.get();
    recordedTrafficEvent.setId(id);
    recordedTrafficEvent.setVehicleId(entity.getVehicleId());
    recordedTrafficEvent.setVehicleBrand(entity.getVehicleBrand());
    recordedTrafficEvent.setTimestamp(entity.getTimestamp());
    return recordedTrafficEvent;
  }

  @Override
  public <VTS extends VehicleBrandTrafficStats> List<VTS> retrieveTrafficStats(
      YearMonth timeWindowFrom,
      YearMonth timeWindowTo,
      Supplier<VTS> vehicleBrandTrafficStatsFactory) {
    return entityRepository
        .findVehicleBrandTrafficStats(toEpochMilli(timeWindowFrom), toEpochMilli(timeWindowTo))
        .map(row -> row.toResponse(vehicleBrandTrafficStatsFactory))
        .toList();
  }

  private long toEpochMilli(YearMonth yearMonth) {
    return yearMonth.atDay(1).atStartOfDay(UTC_ZONE_ID).toInstant().toEpochMilli();
  }
}
