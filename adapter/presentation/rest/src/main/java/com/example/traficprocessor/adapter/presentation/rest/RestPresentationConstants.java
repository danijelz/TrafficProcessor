package com.example.traficprocessor.adapter.presentation.rest;

public interface RestPresentationConstants {
  String INDEX_PATH = "/";

  String API_PART = "api/v1";
  String API_PATH = INDEX_PATH + API_PART;

  String TRAFFIC_EVENTS_API_PART = "/trafficEvents";
  String TRAFFIC_EVENTS_API_PATH = API_PATH + TRAFFIC_EVENTS_API_PART;
  String TRAFFIC_EVENTS_RESOURCE_PART = "/{id}";
  String TRAFFIC_EVENTS_RESOURCE_PATH = TRAFFIC_EVENTS_API_PATH + TRAFFIC_EVENTS_RESOURCE_PART;
  String TRAFFIC_STATS_PART = "/stats";
  String TRAFFIC_STATS_PATH = TRAFFIC_EVENTS_API_PATH + TRAFFIC_STATS_PART;

  String DEV_PART = "dev-api";
  String DEV_PATH = INDEX_PATH + DEV_PART;
  String OPENAPI_PART = "/openapi";
  String OPENAPI_PATH = DEV_PATH + OPENAPI_PART;
  String SWAGGER_PART = "/swagger-ui";
  String SWAGGER_PATH = DEV_PATH + SWAGGER_PART;
  String SWAGGER_INDEX_PART = "/index.html";
  String SWAGGER_INDEX_PATH = SWAGGER_PATH + SWAGGER_INDEX_PART;
  String INDEX_REDIRECT = "redirect:" + SWAGGER_INDEX_PATH;

  String OPEN_API_TITLE = "Traffic Processor Application";
  String OPEN_API_DESCRIPTION =
      """
          Traffic Processor tracks the traffic events and accumulatest statistics \
          about brands of vehicles passing through toll stations.""";
}
