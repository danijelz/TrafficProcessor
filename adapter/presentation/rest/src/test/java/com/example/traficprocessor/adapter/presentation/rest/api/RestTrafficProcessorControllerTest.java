package com.example.traficprocessor.adapter.presentation.rest.api;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_API_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_RESOURCE_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_STATS_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.adapter.presentation.rest.PresentationRestTest;
import com.example.traficprocessor.adapter.presentation.rest.model.RestRecordedTrafficEvent;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficEvent;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficStats;
import com.example.traficprocessor.adapter.presentation.rest.model.RestVehicleBrandTrafficStats;
import com.example.traficprocessor.core.model.TrafficStats;
import java.time.YearMonth;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

public class RestTrafficProcessorControllerTest extends PresentationRestTest {
  @Test
  void givenTrafficEvent_WhenProcessedSuccessfully_ThenResponseContainsHeaderWithLocation()
      throws Exception {
    var trafficEvent = Instancio.create(RestTrafficEvent.class);
    var id = trafficEvent.toId();
    var content = toJson(trafficEvent);
    when(trafficProcessorService.processTrafficEvent(any())).thenReturn(id);
    this.mvc
        .perform(post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(content))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", endsWith(TRAFFIC_EVENTS_API_PATH + "/" + id)));
  }

  @Test
  void
      givenTrafficEvent_WhenProcessedUnsuccessfully_ThenResponseContainsProblemWithLocalizedDescription()
          throws Exception {
    var trafficEvent = Instancio.create(RestTrafficEvent.class);
    var content = toJson(trafficEvent);
    doThrow(IllegalStateException.class).when(trafficProcessorService).processTrafficEvent(any());
    this.mvc
        .perform(
            post(TRAFFIC_EVENTS_API_PATH)
                .header(ACCEPT_LANGUAGE, "sl")
                .contentType(APPLICATION_JSON)
                .content(content))
        .andExpect(status().is(500))
        .andExpect(jsonPath("$.title").value(startsWith("Exception ID:")))
        .andExpect(jsonPath("$.detail").value(startsWith("Ups, nekaj je šlo narobe...")));
  }

  @Test
  void givenTrafficEventId_WhenRetrievingEvent_ThenResponseIsEqualToValueReturnedFromService()
      throws Exception {
    var trafficEvent = Instancio.create(RestRecordedTrafficEvent.class);
    var id = trafficEvent.getId();
    when(trafficProcessorService.retrieveTrafficEvent(any(), any())).thenReturn(trafficEvent);
    this.mvc
        .perform(get(TRAFFIC_EVENTS_RESOURCE_PATH, id).header(ACCEPT_LANGUAGE, "sl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(trafficEvent.getId()))
        .andExpect(jsonPath("$.vehicleId").value(trafficEvent.getVehicleId()))
        .andExpect(jsonPath("$.vehicleBrand").value(trafficEvent.getVehicleBrand().name()))
        .andExpect(jsonPath("$.timestamp").value(trafficEvent.getTimestamp()));
  }

  @Test
  void whenRetrievingTrafficStats_ThenResponseIsEqualToValueReturnedFromService() throws Exception {
    var timeWindowFrom = YearMonth.now();
    var timeWindowTo = timeWindowFrom.plusYears(1);
    var vehicleBrandTrafficStats = Instancio.create(RestVehicleBrandTrafficStats.class);
    var trafficStats =
        new RestTrafficStats(timeWindowFrom, timeWindowTo, List.of(vehicleBrandTrafficStats));

    when(trafficProcessorService.retrieveTrafficStats(any(), any(), any()))
        .thenReturn(trafficStats);
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
        .returns(
            vehicleBrandTrafficStats.getVehicleBrand(),
            RestVehicleBrandTrafficStats::getVehicleBrand)
        .returns(
            vehicleBrandTrafficStats.getNumberOfCountedVehicles(),
            RestVehicleBrandTrafficStats::getNumberOfCountedVehicles);
  }
}
