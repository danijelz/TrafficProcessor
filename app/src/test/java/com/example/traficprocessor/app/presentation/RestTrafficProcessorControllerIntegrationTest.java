package com.example.traficprocessor.app.presentation;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_API_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_RESOURCE_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_STATS_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.adapter.presentation.rest.JacksonRestTestUtils;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficEvent;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficStats;
import com.example.traficprocessor.adapter.presentation.rest.model.RestVehicleBrandTrafficStats;
import com.example.traficprocessor.app.RestIntegrationTest;
import com.example.traficprocessor.core.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.model.TrafficStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.YearMonth;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class RestTrafficProcessorControllerIntegrationTest extends RestIntegrationTest
    implements JacksonRestTestUtils {
  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;

  @Override
  public ObjectMapper mapper() {
    return mapper;
  }

  @Test
  void givenTrafficEvent_WhenProcessedSuccessfully_ThenResponseContainsHeaderWithLocation()
      throws Exception {
    var trafficEvent = Instancio.create(RestTrafficEvent.class);
    var normalizedTrafficEvent = new NormalizedTrafficEvent(trafficEvent);
    var id = normalizedTrafficEvent.toId();
    var content = toJson(trafficEvent);
    this.mvc
        .perform(post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(content))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", endsWith(TRAFFIC_EVENTS_API_PATH + "/" + id)));
  }

  @Test
  void givenTrafficEventId_WhenRetrievingEvent_ThenResponseIsEqualToValueReturnedFromService()
      throws Exception {
    var trafficEvent = Instancio.create(RestTrafficEvent.class);
    var normalizedTrafficEvent = new NormalizedTrafficEvent(trafficEvent);
    var id = normalizedTrafficEvent.toId();
    var content = toJson(trafficEvent);
    this.mvc
        .perform(post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(content))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", endsWith(TRAFFIC_EVENTS_API_PATH + "/" + id)));

    this.mvc
        .perform(get(TRAFFIC_EVENTS_RESOURCE_PATH, id).header(ACCEPT_LANGUAGE, "sl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.vehicleId").value(trafficEvent.getVehicleId()))
        .andExpect(jsonPath("$.vehicleBrand").value(trafficEvent.getVehicleBrand().name()))
        .andExpect(jsonPath("$.timestamp").value(normalizedTrafficEvent.getTimestamp()));
  }

  @Test
  void whenRetrievingTrafficStats_ThenResponseIsEqualToValueReturnedFromService() throws Exception {
    var trafficEvent = Instancio.create(RestTrafficEvent.class);
    var now = Instant.now();
    trafficEvent.setTimestamp(now.toEpochMilli());
    var content = toJson(trafficEvent);
    this.mvc
        .perform(post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(content))
        .andExpect(status().isCreated());

    var timeWindowFrom = YearMonth.now().minusYears(10);
    var timeWindowTo = timeWindowFrom.plusYears(20);

    var result =
        this.mvc
            .perform(
                get(TRAFFIC_STATS_PATH)
                    .header(ACCEPT_LANGUAGE, "sl")
                    .param("timeWindowFrom", timeWindowFrom.toString())
                    .param("timeWindowTo", timeWindowTo.toString()))
            .andExpect(status().isOk())
            .andReturn();

    var responseTrafficStats = fromJson(result, RestTrafficStats.class);
    assertThat(responseTrafficStats)
        .returns(timeWindowFrom, TrafficStats::getTimeWindowFrom)
        .returns(timeWindowTo, TrafficStats::getTimeWindowTo)
        .extracting(TrafficStats::getVehicleBrandTrafficStats)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(1);

    var responseVehicleBrandTrafficStats =
        responseTrafficStats.getVehicleBrandTrafficStats().getFirst();
    assertThat(responseVehicleBrandTrafficStats)
        .returns(trafficEvent.getVehicleBrand(), RestVehicleBrandTrafficStats::getVehicleBrand)
        .returns(1l, RestVehicleBrandTrafficStats::getNumberOfCountedVehicles);
  }
}
