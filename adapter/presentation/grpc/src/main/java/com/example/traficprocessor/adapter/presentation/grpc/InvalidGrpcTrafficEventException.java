package com.example.traficprocessor.adapter.presentation.grpc;

import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficEvent;

public class InvalidGrpcTrafficEventException extends RuntimeException {
  private final GrpcTrafficEvent trafficEvent;

  public InvalidGrpcTrafficEventException(GrpcTrafficEvent trafficEvent) {
    this.trafficEvent = trafficEvent;
  }

  public GrpcTrafficEvent getTrafficEvent() {
    return trafficEvent;
  }
}
