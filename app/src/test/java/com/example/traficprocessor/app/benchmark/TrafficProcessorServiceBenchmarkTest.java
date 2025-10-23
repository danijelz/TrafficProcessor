package com.example.traficprocessor.app.benchmark;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_API_PATH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Benchmark;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc;
import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc.GrpcTrafficProcessorServiceBlockingStub;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcTrafficEvent;
import com.example.traficprocessor.adapter.presentation.grpc.model.GrpcVehicleBrand;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficEvent;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import com.example.traficprocessor.core.model.StubTrafficEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.instancio.Instancio;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.test.web.servlet.MockMvc;

@State(Benchmark)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(MILLISECONDS)
@DisabledInNativeImage
public class TrafficProcessorServiceBenchmarkTest extends BenchmarkTestBase {
  private static MockMvc MVC;
  private static ObjectMapper MAPPER;
  private static TrafficProcessorService TRAFFIC_PROCESSOR_SERVICE;
  private static GrpcTrafficProcessorServiceBlockingStub TRAFFIC_PROCESSOR_GRPC_CONTROLLER;

  @Autowired
  public void setMockMvc(MockMvc mockMvc) {
    MVC = mockMvc;
  }

  @Autowired
  public void setMapper(ObjectMapper mapper) {
    MAPPER = mapper;
  }

  @Autowired
  public void setTrafficProcessor(TrafficProcessorService trafficProcessorService) {
    TRAFFIC_PROCESSOR_SERVICE = trafficProcessorService;
  }

  @Autowired
  public void setGrpcTrafficProcessorController(GrpcChannelFactory channelFactory) {
    var channel = channelFactory.createChannel("localhost:28081");
    TRAFFIC_PROCESSOR_GRPC_CONTROLLER = GrpcTrafficProcessorServiceGrpc.newBlockingStub(channel);
  }

  @Benchmark
  public void processTrafficEventServiceBenchmark() {
    var trafficEvent = Instancio.create(StubTrafficEvent.class);
    TRAFFIC_PROCESSOR_SERVICE.processTrafficEvent(trafficEvent);
  }

  @Benchmark
  public void processTrafficEventRestBenchmark() throws Exception {
    var trafficEvent = Instancio.create(StubTrafficEvent.class);
    var restTrafficEvent =
        toJson(
            new RestTrafficEvent(
                trafficEvent.getTollStationId(),
                trafficEvent.getVehicleId(),
                trafficEvent.getVehicleBrand(),
                trafficEvent.getTimestamp()));
    MVC.perform(
            post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(restTrafficEvent))
        .andExpect(status().isCreated());
  }

  private String toJson(final Object obj) {
    return Try.success(MAPPER).mapTry(m -> m.writeValueAsString(obj)).get();
  }

  @Benchmark
  public void processTrafficEventGrpcBenchmark() {
    var trafficEvent = Instancio.create(StubTrafficEvent.class);
    var grpcTrafficEvent =
        GrpcTrafficEvent.newBuilder()
            .setTollStationId(trafficEvent.getTollStationId())
            .setVehicleId(trafficEvent.getVehicleId())
            .setVehicleBrand(GrpcVehicleBrand.forNumber(trafficEvent.getVehicleBrand().ordinal()))
            .setTimestamp(trafficEvent.getTimestamp())
            .build();
    TRAFFIC_PROCESSOR_GRPC_CONTROLLER.processTrafficEvent(grpcTrafficEvent);
  }
}
