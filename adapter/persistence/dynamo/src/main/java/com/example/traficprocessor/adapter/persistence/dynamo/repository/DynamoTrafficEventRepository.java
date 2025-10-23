package com.example.traficprocessor.adapter.persistence.dynamo.repository;

import static com.example.traficprocessor.adapter.persistence.dynamo.i18n.DynamoI18nInfoConstants.INVALID_ID_MESSAGE;
import static com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus.NOT_FOUND;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues.numberValue;
import static software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues.stringValue;

import com.example.traficprocessor.adapter.persistence.dynamo.entity.DynamoTrafficEventEntity;
import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.domain.repository.TrafficEventRepository;
import com.example.traficprocessor.core.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Supplier;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

public class DynamoTrafficEventRepository implements TrafficEventRepository {
  private static final ZoneId UTC_ZONE_ID = ZoneId.of(UTC.getId());

  private final DynamoDbTemplate dynamoDbTemplate;

  public DynamoTrafficEventRepository(DynamoDbTemplate dynamoDbTemplate) {
    this.dynamoDbTemplate = dynamoDbTemplate;
  }

  @Override
  public String persistTrafficEvent(NormalizedTrafficEvent trafficEvent) {
    // No need for check of existing records, duplicates overwrite the existing
    // value such a way that the semantics of the records are not changed.
    // This is faster than checking for presence by key.
    var id = trafficEvent.toId();
    var entity = new DynamoTrafficEventEntity();
    entity.setId(id);
    entity.setVehicleId(trafficEvent.getVehicleId());
    entity.setVehicleBrand(trafficEvent.getVehicleBrand().ordinal());
    entity.setTimestamp(trafficEvent.getTimestamp());
    dynamoDbTemplate.save(entity);

    return id;
  }

  @Override
  public <TE extends RecordedTrafficEvent> TE retrieveTrafficEvent(
      String id, Supplier<TE> trafficEventFactory) {
    var entity = findTrafficEventEntity(id);
    return toRecordedTrafficEvent(id, entity, trafficEventFactory);
  }

  private <TE extends RecordedTrafficEvent> TE toRecordedTrafficEvent(
      String id, DynamoTrafficEventEntity entity, Supplier<TE> recordedTrafficEventFactory) {
    var recordedTrafficEvent = recordedTrafficEventFactory.get();
    recordedTrafficEvent.setId(id);
    recordedTrafficEvent.setVehicleId(entity.getVehicleId());
    recordedTrafficEvent.setVehicleBrand(VehicleBrand.values()[entity.getVehicleBrand()]);
    recordedTrafficEvent.setTimestamp(entity.getTimestamp());
    return recordedTrafficEvent;
  }

  private DynamoTrafficEventEntity findTrafficEventEntity(String id) {
    var expression =
        Expression.builder()
            .expression("#id = :id")
            .putExpressionName("#id", "id")
            .putExpressionValue(":id", stringValue(id))
            .build();
    var scanRequest = ScanEnhancedRequest.builder().filterExpression(expression).build();
    return dynamoDbTemplate.scan(scanRequest, DynamoTrafficEventEntity.class).items().stream()
        .findFirst()
        .orElseThrow(
            () -> new ServiceException(NOT_FOUND, INVALID_ID_MESSAGE.toMessage(id.toString())));
  }

  @Override
  public <VTS extends VehicleBrandTrafficStats> List<VTS> retrieveTrafficStats(
      YearMonth timeWindowFrom,
      YearMonth timeWindowTo,
      Supplier<VTS> vehicleBrandTrafficStatsFactory) {
    var expression =
        Expression.builder()
            .expression("#timestamp >= :timeWindowFrom AND #timestamp <= :timeWindowTo")
            .putExpressionName("#timestamp", "timestamp")
            .putExpressionValue(":timeWindowFrom", numberValue(toEpochMilli(timeWindowFrom)))
            .putExpressionValue(":timeWindowTo", numberValue(toEpochMilli(timeWindowTo)))
            .build();

    var scanRequest = ScanEnhancedRequest.builder().filterExpression(expression).build();
    return dynamoDbTemplate.scan(scanRequest, DynamoTrafficEventEntity.class).items().stream()
        .collect(groupingBy(DynamoTrafficEventEntity::getVehicleBrand, counting()))
        .entrySet()
        .stream()
        .map(e -> toResponse(e.getKey(), e.getValue(), vehicleBrandTrafficStatsFactory))
        .toList();
  }

  private long toEpochMilli(YearMonth yearMonth) {
    return yearMonth.atDay(1).atStartOfDay(UTC_ZONE_ID).toInstant().toEpochMilli();
  }

  private <VTS extends VehicleBrandTrafficStats> VTS toResponse(
      int vehicleBrand,
      long numberOfCountedVehicles,
      Supplier<VTS> vehicleBrandTrafficStatsFactory) {
    var response = vehicleBrandTrafficStatsFactory.get();
    response.setVehicleBrand(VehicleBrand.values()[vehicleBrand]);
    response.setNumberOfCountedVehicles(numberOfCountedVehicles);
    return response;
  }
}
