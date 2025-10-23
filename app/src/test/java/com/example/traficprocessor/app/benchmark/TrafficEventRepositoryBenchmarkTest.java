package com.example.traficprocessor.app.benchmark;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

import com.example.traficprocessor.adapter.persistence.dynamo.repository.DynamoTrafficEventRepository;
import com.example.traficprocessor.adapter.persistence.jpa.repository.JpaTrafficEventRepository;
import com.example.traficprocessor.core.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.model.StubTrafficEvent;
import org.instancio.Instancio;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.springframework.beans.factory.annotation.Autowired;

@State(Benchmark)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(MILLISECONDS)
@DisabledInNativeImage
public class TrafficEventRepositoryBenchmarkTest extends BenchmarkTestBase {
  private static JpaTrafficEventRepository JPA_TRAFFIC_EVENT_REPOSITORY;
  private static DynamoTrafficEventRepository DYNAMO_TRAFFIC_EVENT_REPOSITORY;

  @Autowired
  public void setJpaTrafficEventRepository(JpaTrafficEventRepository jpaTrafficEventRepository) {
    JPA_TRAFFIC_EVENT_REPOSITORY = jpaTrafficEventRepository;
  }

  @Autowired
  public void setMapper(DynamoTrafficEventRepository dynamoTrafficEventRepository) {
    DYNAMO_TRAFFIC_EVENT_REPOSITORY = dynamoTrafficEventRepository;
  }

  @Benchmark
  public void persistTrafficEventJpaBenchmark() throws Exception {
    var trafficEvent = Instancio.create(StubTrafficEvent.class);
    JPA_TRAFFIC_EVENT_REPOSITORY.persistTrafficEvent(new NormalizedTrafficEvent(trafficEvent));
  }

  @Benchmark
  public void persistTrafficEventDynamoBenchmark() {
    var trafficEvent = Instancio.create(StubTrafficEvent.class);
    DYNAMO_TRAFFIC_EVENT_REPOSITORY.persistTrafficEvent(new NormalizedTrafficEvent(trafficEvent));
  }
}
