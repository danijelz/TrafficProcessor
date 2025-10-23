package com.example.traficprocessor.adapter.presentation.rest.observability;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

class ClientRequestLogger implements ClientHttpRequestInterceptor {
  private static final Logger LOGGER = getLogger(ClientRequestLogger.class);

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    LOGGER.info("ClientRequest: {} {}", request.getMethod(), request.getURI());
    return execution.execute(request, body);
  }
}
