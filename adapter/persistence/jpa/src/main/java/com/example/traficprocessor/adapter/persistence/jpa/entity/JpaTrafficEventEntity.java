package com.example.traficprocessor.adapter.persistence.jpa.entity;

import static com.example.traficprocessor.core.domain.model.TrafficEventConstraints.MIN_TIMESTAMP;
import static com.example.traficprocessor.core.domain.model.TrafficEventConstraints.MIN_VEHICLE_ID_LENGTH;

import com.example.traficprocessor.adapter.persistence.jpa.entity.JpaTrafficEventIdGenerator.JpaTrafficEventIdGeneratorType;
import com.example.traficprocessor.core.model.IdentifiableTrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity(name = "TrafficEvent")
@Table(indexes = @Index(name = "IDX_Traffic_Event_Timestamp", columnList = "timestamp"))
public class JpaTrafficEventEntity implements IdentifiableTrafficEvent {
  private String id;
  private String vehicleId;
  private VehicleBrand vehicleBrand;
  private long timestamp;

  @Id
  @JpaTrafficEventIdGeneratorType
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  @NotNull
  @Size(min = MIN_VEHICLE_ID_LENGTH)
  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  @NotNull
  @Enumerated
  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(VehicleBrand vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Override
  @Min(MIN_TIMESTAMP)
  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
