package com.example.traficprocessor.adapter.presentation.rest;

import com.example.traficprocessor.adapter.presentation.rest.api.HomeRestController;
import com.example.traficprocessor.adapter.presentation.rest.api.RestTrafficProcessorController;
import com.example.traficprocessor.adapter.presentation.rest.hateoas.ModelRepresentationController;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
  HomeRestController.class,
  ModelRepresentationController.class,
  RestTrafficProcessorController.class
})
public abstract class PresentationRestTest implements JacksonRestTestUtils {
  @Autowired protected MockMvc mvc;
  @Autowired protected ObjectMapper mapper;
  @MockitoBean protected TrafficProcessorService trafficProcessorService;

  @Override
  public ObjectMapper mapper() {
    return mapper;
  }
}
