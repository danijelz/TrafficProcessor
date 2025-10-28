package com.example.traficprocessor.adapter.security.oauth;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.INDEX_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_RESOURCE_PATH;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.adapter.presentation.rest.PresentationRestTest;
import com.example.traficprocessor.adapter.presentation.rest.model.RestRecordedTrafficEvent;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

public class RestSecurityOauthTest extends PresentationRestTest {
  @Test
  void givenUnauthenticatedRequestToIndex_WhenProcessed_ThenRequestMustSucceed() throws Exception {
    mvc.perform(get(INDEX_PATH)).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void givenAuthenticatedRequestToIndex_WhenProcessed_ThenRequestMustSucceed() throws Exception {
    mvc.perform(get(INDEX_PATH)).andExpect(status().is3xxRedirection());
  }

  @Test
  void givenUnauthenticatedRequestToTrafficEventResource_WhenProcessed_ThenRequestMustBeRejected()
      throws Exception {
    mvc.perform(get(TRAFFIC_EVENTS_RESOURCE_PATH, randomString()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void givenValidTrafficEventId_WhenRetrievingEvent_ThenResponseIsEqualToValueReturnedFromService()
      throws Exception {
    var trafficEvent = Instancio.create(RestRecordedTrafficEvent.class);
    var id = trafficEvent.getId();
    when(trafficProcessorService.retrieveTrafficEvent(any(), any())).thenReturn(trafficEvent);
    mvc.perform(get(TRAFFIC_EVENTS_RESOURCE_PATH, id)).andExpect(status().isOk());
  }
}
