package com.example.traficprocessor.app.presentation;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.INDEX_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.SWAGGER_INDEX_PATH;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.app.RestIntegrationTest;
import org.junit.jupiter.api.Test;

public class IndexControllerIntegrationTest extends RestIntegrationTest {
  @Test
  public void givenRequestToIndex_WhenProcessed_ThenResponseContainsRedirectionToSwagger()
      throws Exception {
    this.mvc
        .perform(get(INDEX_PATH))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SWAGGER_INDEX_PATH));
  }

  @Test
  public void givenRequestToSwaggerIndex_WhenProcessed_ThenResponseContainsSwaggerHtmlPage()
      throws Exception {
    this.mvc
        .perform(get(SWAGGER_INDEX_PATH))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TEXT_HTML));
  }
}
