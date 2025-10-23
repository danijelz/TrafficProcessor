package com.example.traficprocessor.adapter.persistence.dynamo.entity;

import com.example.traficprocessor.core.model.IdentifiableTrafficEvent;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@TableName("TrafficEvent")
public class DynamoTrafficEventEntity implements IdentifiableTrafficEvent {
  private String id;
  private String vehicleId;
  private int vehicleBrand;
  private long timestamp;

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  public int getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(int vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @DynamoDbSortKey
  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
