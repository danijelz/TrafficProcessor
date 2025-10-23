package com.example.traficprocessor.adapter.persistence.jpa.repository;

import com.example.traficprocessor.adapter.persistence.jpa.entity.JpaTrafficEventEntity;
import com.example.traficprocessor.core.model.VehicleBrand;
import com.example.traficprocessor.core.model.VehicleBrandTrafficStats;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaTrafficEventEntityRepository
    extends JpaRepository<JpaTrafficEventEntity, String> {
  @Query(
      """
          SELECT te.vehicleBrand, COUNT(*)
          FROM   TrafficEvent te
          WHERE  te.timestamp >= ?1
          AND    te.timestamp <= ?2
          GROUP BY te.vehicleBrand""")
  Stream<VehicleBrandTrafficStatsRow> findVehicleBrandTrafficStats(
      long timeWindowFrom, long timeWindowTo);

  record VehicleBrandTrafficStatsRow(VehicleBrand vehicleBrand, long numberOfCountedVehicles) {
    public <VTS extends VehicleBrandTrafficStats> VTS toResponse(
        Supplier<VTS> vehicleBrandTrafficStatsFactory) {
      var response = vehicleBrandTrafficStatsFactory.get();
      response.setVehicleBrand(vehicleBrand);
      response.setNumberOfCountedVehicles(numberOfCountedVehicles);
      return response;
    }
  }
}
