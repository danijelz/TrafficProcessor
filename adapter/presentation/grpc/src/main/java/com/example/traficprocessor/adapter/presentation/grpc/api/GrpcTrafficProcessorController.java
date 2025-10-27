package com.example.traficprocessor.adapter.presentation.grpc.api;

import static com.example.traficprocessor.adapter.presentation.grpc.model.GrpcYearMonthAdapter.fromGrpcYearMonth;

import com.example.traficprocessor.adapter.presentation.grpc.InvalidGrpcTrafficEventException;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@GrpcService
public class GrpcTrafficProcessorController extends GrpcTrafficProcessorServiceImplBase {
  private final TrafficProcessorService trafficProcessorService;
  private final LocalValidatorFactoryBean validatorFactoryBean;

  public GrpcTrafficProcessorController(
      TrafficProcessorService trafficProcessorService,
      LocalValidatorFactoryBean validatorFactoryBean) {
    this.trafficProcessorService = trafficProcessorService;
    this.validatorFactoryBean = validatorFactoryBean;
  }

  @Override
  public void processTrafficEvent(
      GrpcTrafficEvent request, StreamObserver<StringValue> responseObserver) {
    var trafficEvent = new GrpcTrafficEventAdapter(request);
    validatorFactoryBean
        .validateObject(trafficEvent)
        .failOnError(_ -> new InvalidGrpcTrafficEventException(request));
    var id = trafficProcessorService.processTrafficEvent(trafficEvent);
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
