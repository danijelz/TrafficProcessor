package com.example.traficprocessor.adapter.security.oauth;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.INDEX_PATH;
import static com.example.traficprocessor.core.domain.utils.Randoms.randomString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.traficprocessor.adapter.security.oauth.SecurityOauthTest.SecurityOauthTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(SecurityOauthTestController.class)
public class SecurityOauthTest {
  @Autowired protected MockMvc mvc;

  @Test
  void givenUnauthenticatedRequestToIndex_WhenProcessed_ThenRequestMustBeRejected()
      throws Exception {
    mvc.perform(get(INDEX_PATH)).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void givenAuthenticatedRequestToIndex_WhenProcessed_ThenRequestMustSucceed() throws Exception {
    mvc.perform(get(INDEX_PATH)).andExpect(status().isOk());
  }

  @SpringBootApplication
  @Import(SecurityOauthTestController.class)
  static class SecurityOauthConfig {}

  @Controller
  @RestController
  static class SecurityOauthTestController {
    @GetMapping(INDEX_PATH)
    public String index() {
      return randomString();
    }
  }
}
