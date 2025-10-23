package com.example.traficprocessor.app.repository;

import com.example.traficprocessor.adapter.persistence.dynamo.repository.DynamoTrafficEventRepository;
import com.example.traficprocessor.adapter.persistence.jpa.repository.JpaTrafficEventRepository;
import com.example.traficprocessor.core.domain.repository.TrafficEventRepository;
import com.example.traficprocessor.core.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.model.RecordedTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

public class CompositeTrafficEventRepository implements TrafficEventRepository {
  private final JpaTrafficEventRepository jpaRepository;
  private final DynamoTrafficEventRepository dynamoRepository;

  public CompositeTrafficEventRepository(
      JpaTrafficEventRepository jpaRepository, DynamoTrafficEventRepository dynamoRepository) {
    this.jpaRepository = jpaRepository;
    this.dynamoRepository = dynamoRepository;
  }

  @Override
  public String persistTrafficEvent(NormalizedTrafficEvent trafficEvent) {
    jpaRepository.persistTrafficEvent(trafficEvent);
    dynamoRepository.persistTrafficEvent(trafficEvent);
    return trafficEvent.toId();
  }

  @Override
  public <TE extends RecordedTrafficEvent> TE retrieveTrafficEvent(
      String id, Supplier<TE> trafficEventFactory) {
    return dynamoRepository.retrieveTrafficEvent(id, trafficEventFactory);
  }

  @Override
  public <VTS extends VehicleBrandTrafficStats> List<VTS> retrieveTrafficStats(
      YearMonth timeWindowFrom,
      YearMonth timeWindowTo,
      Supplier<VTS> vehicleBrandTrafficStatsFactory) {
    return dynamoRepository.retrieveTrafficStats(
        timeWindowFrom, timeWindowTo, vehicleBrandTrafficStatsFactory);
  }
}
