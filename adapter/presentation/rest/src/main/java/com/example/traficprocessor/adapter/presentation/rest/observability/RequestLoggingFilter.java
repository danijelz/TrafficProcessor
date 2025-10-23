package com.example.traficprocessor.adapter.presentation.rest.observability;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {
  private static final String HEALTH_PATH_PART = "/actuator/health";
  private static final String PROMETHEUS_PATH_PART = "/actuator/prometheus";

  public RequestLoggingFilter() {
    setBeforeMessagePrefix("");
    setBeforeMessageSuffix("");
    setIncludeClientInfo(true);
    setIncludeQueryString(true);
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    var requestURI = request.getRequestURI();
    return requestURI == null
        || requestURI.isBlank()
        || (!requestURI.contains(HEALTH_PATH_PART) && !requestURI.contains(PROMETHEUS_PATH_PART));
  }

  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
    getServletContext().log(message);
  }

  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    // ignored
  }
}
