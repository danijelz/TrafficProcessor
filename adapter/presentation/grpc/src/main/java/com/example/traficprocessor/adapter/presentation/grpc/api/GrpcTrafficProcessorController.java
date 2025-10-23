package com.example.traficprocessor.adapter.presentation.grpc.api;

import static com.example.traficprocessor.adapter.presentation.grpc.model.GrpcYearMonthAdapter.fromGrpcYearMonth;

import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc.GrpcTrafficProcessorServiceImplBase;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcRecordedTrafficEvent;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcRecordedTrafficEventAdapter;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficEvent;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficEventAdapter;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficStats;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficStatsRequest;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTraficStatsFactory;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class GrpcTrafficProcessorController extends GrpcTrafficProcessorServiceImplBase {
  private final TrafficProcessorService trafficProcessorService;

  public GrpcTrafficProcessorController(TrafficProcessorService trafficProcessorService) {
    this.trafficProcessorService = trafficProcessorService;
  }

  @Override
  public void processTrafficEvent(
      GrpcTrafficEvent request, StreamObserver<StringValue> responseObserver) {
    var id = trafficProcessorService.processTrafficEvent(new GrpcTrafficEventAdapter(request));
    responseObserver.onNext(StringValue.of(id));
    responseObserver.onCompleted();
  }

  @Override
  public void retrieveTrafficEvent(
      StringValue request, StreamObserver<GrpcRecordedTrafficEvent> responseObserver) {
    var trafficEvent =
        trafficProcessorService
            .retrieveTrafficEvent(request.getValue(), GrpcRecordedTrafficEventAdapter::new)
            .build();
    responseObserver.onNext(trafficEvent);
    responseObserver.onCompleted();
  }

  @Override
  public void retrieveTrafficStats(
      GrpcTrafficStatsRequest request, StreamObserver<GrpcTrafficStats> responseObserver) {
    var timeWindowFrom = fromGrpcYearMonth(request.getTimeWindowFrom());
    var timeWindowTo = fromGrpcYearMonth(request.getTimeWindowTo());
    var traficStats =
        trafficProcessorService
            .retrieveTrafficStats(timeWindowFrom, timeWindowTo, GrpcTraficStatsFactory.INSTANCE)
            .build();
    responseObserver.onNext(traficStats);
    responseObserver.onCompleted();
  }
}
