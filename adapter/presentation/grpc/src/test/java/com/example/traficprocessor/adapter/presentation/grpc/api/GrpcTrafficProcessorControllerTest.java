package com.example.traficprocessor.adapter.presentation.grpc.api;

import static com.example.traficprocessor.adapter.presentation.grpc.model.GrpcVehicleBrand.forNumber;
import static com.example.traficprocessor.adapter.presentation.grpc.model.GrpcYearMonthAdapter.toGrpcYearMonth;
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.INVALID_ARGUMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.traficprocessor.adapter.presentation.grpc.PresentationGrpcTest;
import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc.GrpcTrafficProcessorServiceBlockingStub;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcRecordedTrafficEvent;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcRecordedTrafficEventAdapter;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficEvent;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficStats;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficStatsAdapter;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficStatsRequest;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcVehicleBrandTrafficStats;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcVehicleBrandTrafficStatsAdapter;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import com.example.traficprocessor.core.model.StubRecordedTrafficEvent;
import com.example.traficprocessor.core.model.StubTrafficEvent;
import com.example.traficprocessor.core.model.StubVehicleBrandTrafficStats;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.YearMonth;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@PresentationGrpcTest
public class GrpcTrafficProcessorControllerTest {
  @Autowired private GrpcTrafficProcessorServiceBlockingStub stub;
  @MockitoBean private TrafficProcessorService trafficProcessorService;

  @Test
  void givenTrafficEvent_WhenProcessedSuccessfully_ThenResponseContainsValidId() throws Exception {
    var stubTrafficEvent = Instancio.create(StubTrafficEvent.class);
    var id = stubTrafficEvent.toId();
    var trafficEvent = toGrpcTrafficEvent(stubTrafficEvent);
    when(trafficProcessorService.processTrafficEvent(any())).thenReturn(id);
    var response = stub.processTrafficEvent(trafficEvent);
    assertThat(response.getValue()).isEqualTo(id);
  }

  @Test
  void
      givenTrafficEventWithInvalidVehicleId_WhenProcessing_ThenExceptionLocalizedDescriptionIsThrown()
          throws Exception {
    var trafficEventWithNullvehicleId =
        Instancio.of(StubTrafficEvent.class)
            .generate(field("vehicleId"), gen -> gen.string().maxLength(2))
            .create();
    var trafficEvent = toGrpcTrafficEvent(trafficEventWithNullvehicleId);
    assertThatExceptionOfType(StatusRuntimeException.class)
        .isThrownBy(() -> stub.processTrafficEvent(trafficEvent))
        .extracting(StatusRuntimeException::getStatus)
        .returns(INVALID_ARGUMENT.getCode(), Status::getCode)
        .extracting(Status::getDescription)
        .asString()
        .contains("Neveljaven TrafficEvent");
  }

  @Test
  void givenTrafficEvent_WhenProcessedUnsuccessfully_ThenExceptionLocalizedDescriptionIsThrown()
      throws Exception {
    var stubTrafficEvent = Instancio.create(StubTrafficEvent.class);
    var trafficEvent = toGrpcTrafficEvent(stubTrafficEvent);
    doThrow(IllegalStateException.class).when(trafficProcessorService).processTrafficEvent(any());
    assertThatExceptionOfType(StatusRuntimeException.class)
        .isThrownBy(() -> stub.processTrafficEvent(trafficEvent))
        .extracting(StatusRuntimeException::getStatus)
        .returns(INTERNAL.getCode(), Status::getCode)
        .extracting(Status::getDescription)
        .asString()
        .contains("Ups, nekaj je šlo narobe...");
  }

  private GrpcTrafficEvent toGrpcTrafficEvent(StubTrafficEvent stubTrafficEvent) {
    return GrpcTrafficEvent.newBuilder()
        .setTollStationId(stubTrafficEvent.getTollStationId())
        .setVehicleId(stubTrafficEvent.getVehicleId())
        .setVehicleBrand(forNumber(stubTrafficEvent.getVehicleBrand().ordinal()))
        .setTimestamp(stubTrafficEvent.getTimestamp())
        .build();
  }

  @Test
  void givenTrafficEventId_WhenRetrievingEvent_ThenResultIsEqualToValueReturnedFromService()
      throws Exception {
    var stubTrafficEvent = Instancio.create(StubRecordedTrafficEvent.class);
    var trafficEvent = toGrpcRecordedTrafficEvent(stubTrafficEvent);
    when(trafficProcessorService.retrieveTrafficEvent(any(), any())).thenReturn(trafficEvent);
    var id = stubTrafficEvent.getId();
    var response = stub.retrieveTrafficEvent(StringValue.of(id));
    assertThat(response)
        .returns(stubTrafficEvent.getId(), GrpcRecordedTrafficEvent::getId)
        .returns(stubTrafficEvent.getVehicleId(), GrpcRecordedTrafficEvent::getVehicleId)
        .returns(
            stubTrafficEvent.getVehicleBrand().ordinal(),
            GrpcRecordedTrafficEvent::getVehicleBrandValue)
        .returns(stubTrafficEvent.getTimestamp(), GrpcRecordedTrafficEvent::getTimestamp);
  }

  private GrpcRecordedTrafficEventAdapter toGrpcRecordedTrafficEvent(
      StubRecordedTrafficEvent stubTrafficEvent) {
    var adapter = new GrpcRecordedTrafficEventAdapter();
    adapter.setId(stubTrafficEvent.getId());
    adapter.setVehicleId(stubTrafficEvent.getVehicleId());
    adapter.setVehicleBrand(stubTrafficEvent.getVehicleBrand());
    adapter.setTimestamp(stubTrafficEvent.getTimestamp());
    return adapter;
  }

  @Test
  void whenRetrievingTrafficStats_ThenResponseIsEqualToValueReturnedFromService() throws Exception {
    var timeWindowFrom = YearMonth.now();
    var timeWindowTo = timeWindowFrom.plusYears(1);
    var vehicleBrandTrafficStats = Instancio.create(StubVehicleBrandTrafficStats.class);
    var grpcVehicleBrandTrafficStats = new GrpcVehicleBrandTrafficStatsAdapter();
    grpcVehicleBrandTrafficStats.setVehicleBrand(vehicleBrandTrafficStats.getVehicleBrand());
    grpcVehicleBrandTrafficStats.setNumberOfCountedVehicles(
        vehicleBrandTrafficStats.getNumberOfCountedVehicles());

    var trafficStats = new GrpcTrafficStatsAdapter();
    trafficStats.setTimeWindowFrom(timeWindowFrom);
    trafficStats.setTimeWindowTo(timeWindowTo);
    trafficStats.setVehicleBrandTrafficStats(List.of(grpcVehicleBrandTrafficStats));

    when(trafficProcessorService.retrieveTrafficStats(any(), any(), any()))
        .thenReturn(trafficStats);

    var request =
        GrpcTrafficStatsRequest.newBuilder()
            .setTimeWindowFrom(toGrpcYearMonth(timeWindowFrom))
            .setTimeWindowTo(toGrpcYearMonth(timeWindowTo))
            .build();
    var response = stub.retrieveTrafficStats(request);

    assertThat(response)
        .returns(toGrpcYearMonth(timeWindowFrom), GrpcTrafficStats::getTimeWindowFrom)
        .returns(toGrpcYearMonth(timeWindowTo), GrpcTrafficStats::getTimeWindowTo)
        .extracting(GrpcTrafficStats::getVehicleBrandTrafficStatsList)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(1);

    var responseVehicleBrandTrafficStats = response.getVehicleBrandTrafficStatsList().getFirst();
    assertThat(responseVehicleBrandTrafficStats)
        .returns(
            vehicleBrandTrafficStats.getVehicleBrand().ordinal(),
            GrpcVehicleBrandTrafficStats::getVehicleBrandValue)
        .returns(
            vehicleBrandTrafficStats.getNumberOfCountedVehicles(),
            GrpcVehicleBrandTrafficStats::getNumberOfCountedVehicles);
  }
}
