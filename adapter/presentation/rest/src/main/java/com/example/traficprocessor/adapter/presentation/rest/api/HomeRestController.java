package com.example.traficprocessor.adapter.presentation.rest.api;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.INDEX_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.INDEX_REDIRECT;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Home redirection to OpenAPI api documentation */
@Controller
public class HomeRestController {
  @GetMapping(INDEX_PATH)
  public String index() {
    return INDEX_REDIRECT;
  }
}
