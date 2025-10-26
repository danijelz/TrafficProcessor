package com.example.traficprocessor.adapter.presentation.rest.hateoas;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.API_PATH;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.adapter.presentation.rest.PresentationRestTest;
import org.junit.jupiter.api.Test;

public class ModelRepresentationControllerTest extends PresentationRestTest {
  @Test
  void givenRequestToApiPath_WhenProcessed_ThenResponseContainsHateoasModel() throws Exception {
    mvc.perform(get(API_PATH))
        .andExpect(status().isOk())
        .andExpect(content().contentType(HAL_JSON))
        .andExpect(jsonPath("$._links.self").exists())
        .andExpect(jsonPath("$._links.processTrafficEvent").exists())
        .andExpect(jsonPath("$._links.retrieveTrafficEvent").exists())
        .andExpect(jsonPath("$._links.retrieveTrafficStats").exists());
  }
}
