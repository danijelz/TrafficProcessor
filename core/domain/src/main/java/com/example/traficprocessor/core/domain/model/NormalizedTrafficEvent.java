package com.example.traficprocessor.core.domain.model;

import static com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus.BAD_REQUST;
import static com.example.traficprocessor.core.domain.i18n.DomainI18nInfoConstants.INVALID_TRAFFIC_EVENT_MESSAGE;
import static com.example.traficprocessor.core.domain.model.TrafficEventConstraints.isValidTrafficEvent;
import static com.example.traficprocessor.core.model.IdentifiableTrafficEvent.normalizeTimestamp;

import com.example.traficprocessor.core.domain.exception.ServiceException;
import com.example.traficprocessor.core.model.IdentifiableTrafficEvent;
import com.example.traficprocessor.core.model.TrafficEvent;
import com.example.traficprocessor.core.model.VehicleBrand;

public record NormalizedTrafficEvent(String vehicleId, VehicleBrand vehicleBrand, long timestamp)
    implements IdentifiableTrafficEvent {
  public NormalizedTrafficEvent(TrafficEvent trafficEvent) {
    this(trafficEvent.getVehicleId(), trafficEvent.getVehicleBrand(), trafficEvent.getTimestamp());
  }

  public NormalizedTrafficEvent(String vehicleId, VehicleBrand vehicleBrand, long timestamp) {
    if (!isValidTrafficEvent(vehicleId, vehicleBrand, timestamp)) {
      var message = INVALID_TRAFFIC_EVENT_MESSAGE.toMessage(vehicleId, vehicleBrand, timestamp);
      throw new ServiceException(BAD_REQUST, message);
    }

    this.vehicleId = vehicleId;
    this.vehicleBrand = vehicleBrand;
    this.timestamp = normalizeTimestamp(timestamp);
  }

  @Override
  public String toId() {
    return timestamp + TIMESTAMP_VEHICLE_ID_DELIMITER + vehicleId;
  }

  public String getVehicleId() {
    return vehicleId;
  }

  public VehicleBrand getVehicleBrand() {
    return vehicleBrand;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
