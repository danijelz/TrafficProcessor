package com.example.traficprocessor.adapter.presentation.rest.api;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.INDEX_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.SWAGGER_INDEX_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.adapter.presentation.rest.PresentationRestTest;
import org.junit.jupiter.api.Test;

public class IndexControllerTest extends PresentationRestTest {
  @Test
  void givenRequestToIndex_WhenProcessed_ThenResponseContainsRedirectionToSwagger()
      throws Exception {
    this.mvc
        .perform(get(INDEX_PATH))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SWAGGER_INDEX_PATH));
  }
}
