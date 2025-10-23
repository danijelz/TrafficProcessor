package com.example.traficprocessor.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
public abstract class RestIntegrationTest {
  @Autowired protected MockMvc mvc;
}
