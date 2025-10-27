package com.example.traficprocessor.app.presentation;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_API_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_RESOURCE_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_STATS_PATH;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.instancio.Select.field;
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
import com.example.traficprocessor.core.domain.model.NormalizedTrafficEvent;
import com.example.traficprocessor.core.model.TrafficStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.YearMonth;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RestTrafficProcessorControllerIntegrationTest extends RestIntegrationTest
    implements JacksonRestTestUtils {
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
    mvc.perform(post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(content))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", endsWith(TRAFFIC_EVENTS_API_PATH + "/" + id)));
  }

  @Test
  void givenTrafficEventWithInvalidVehicleId_WhenProcessing_ThenProblemLocalizedDescriptionIsReturned()
      throws Exception {
    var trafficEventWithNullvehicleId =
        Instancio.of(RestTrafficEvent.class).set(field("vehicleId"), null).create();
    var content = toJson(trafficEventWithNullvehicleId);
    mvc.perform(
            post(TRAFFIC_EVENTS_API_PATH)
                .header(ACCEPT_LANGUAGE, "sl")
                .contentType(APPLICATION_JSON)
                .content(content))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(startsWith("Exception ID:")))
        .andExpect(jsonPath("$.detail").value(startsWith("Neveljaven TrafficEvent")));

    var trafficEventWithShortVehicleId =
        Instancio.of(RestTrafficEvent.class)
            .generate(field("vehicleId"), gen -> gen.string().maxLength(2))
            .create();
    content = toJson(trafficEventWithShortVehicleId);
    mvc.perform(
            post(TRAFFIC_EVENTS_API_PATH)
                .header(ACCEPT_LANGUAGE, "sl")
                .contentType(APPLICATION_JSON)
                .content(content))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(startsWith("Exception ID:")))
        .andExpect(jsonPath("$.detail").value(startsWith("Neveljaven TrafficEvent")));
  }

  @Test
  void givenTrafficEventWithInvalidVehicleBrand_WhenProcessing_ThenProblemWithLocalizedDescriptionIsReturned()
      throws Exception {
    var trafficEvent =
        Instancio.of(RestTrafficEvent.class).set(field("vehicleBrand"), null).create();
    var content = toJson(trafficEvent);

    mvc.perform(
            post(TRAFFIC_EVENTS_API_PATH)
                .header(ACCEPT_LANGUAGE, "sl")
                .contentType(APPLICATION_JSON)
                .content(content))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(startsWith("Exception ID:")))
        .andExpect(jsonPath("$.detail").value(startsWith("Neveljaven TrafficEvent")));
  }

  @Test
  void givenTrafficEventWithInvalidTimestamp_WhenProcessing_ThenProblemLocalizedDescriptionIsReturned()
      throws Exception {
    var trafficEvent =
        Instancio.of(RestTrafficEvent.class)
            .generate(field("timestamp"), gen -> gen.longs().max(-1l))
            .create();
    var content = toJson(trafficEvent);

    mvc.perform(
            post(TRAFFIC_EVENTS_API_PATH)
                .header(ACCEPT_LANGUAGE, "sl")
                .contentType(APPLICATION_JSON)
                .content(content))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(startsWith("Exception ID:")))
        .andExpect(jsonPath("$.detail").value(startsWith("Neveljaven TrafficEvent")));
  }

  @Test
  void givenValidTrafficEventId_WhenRetrievingEvent_ThenResponseIsEqualToValueReturnedFromService()
      throws Exception {
    var trafficEvent = Instancio.create(RestTrafficEvent.class);
    var normalizedTrafficEvent = new NormalizedTrafficEvent(trafficEvent);
    var id = normalizedTrafficEvent.toId();
    var content = toJson(trafficEvent);
    mvc.perform(post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(content))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", endsWith(TRAFFIC_EVENTS_API_PATH + "/" + id)));

    mvc.perform(get(TRAFFIC_EVENTS_RESOURCE_PATH, id).header(ACCEPT_LANGUAGE, "sl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.vehicleId").value(trafficEvent.getVehicleId()))
        .andExpect(jsonPath("$.vehicleBrand").value(trafficEvent.getVehicleBrand().name()))
        .andExpect(jsonPath("$.timestamp").value(normalizedTrafficEvent.getTimestamp()));
  }

  @Test
  void givenInvalidTrafficEventId_WhenRetrieveingTrafficEventByInvalidId_ThenProblemLocalizedDescriptionIsReturned()
      throws Exception {
    mvc.perform(get(TRAFFIC_EVENTS_RESOURCE_PATH, randomString(2)).header(ACCEPT_LANGUAGE, "sl"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(startsWith("Exception ID:")))
        .andExpect(jsonPath("$.detail").value(startsWith("Neveljavn ID vozila")));
  }

  @Test
  void whenRetrievingTrafficStats_ThenResponseIsEqualToValueReturnedFromService() throws Exception {
    var trafficEvent = Instancio.create(RestTrafficEvent.class);
    var now = Instant.now();
    trafficEvent.setTimestamp(now.toEpochMilli());
    var content = toJson(trafficEvent);
    mvc.perform(post(TRAFFIC_EVENTS_API_PATH).contentType(APPLICATION_JSON).content(content))
        .andExpect(status().isCreated());

    var timeWindowFrom = YearMonth.now().minusYears(10);
    var timeWindowTo = timeWindowFrom.plusYears(20);

    var result =
        mvc.perform(
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
