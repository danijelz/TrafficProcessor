package com.example.traficprocessor.adapter.presentation.rest.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

import com.example.traficprocessor.adapter.presentation.rest.api.RestTrafficProcessorController;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficStats;
import java.util.Optional;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;

public class TrafficStatsModelAssembler
    implements SimpleRepresentationModelAssembler<RestTrafficStats> {
  private static final Class<RestTrafficProcessorController> CONTROLLER =
      RestTrafficProcessorController.class;

  @Override
  public void addLinks(EntityModel<RestTrafficStats> resource) {
    var trafficStats = resource.getContent();
    var controllerProxy = methodOn(CONTROLLER);
    var timeWindowFrom = trafficStats.getTimeWindowFrom();
    var timeWindowTo = Optional.of(trafficStats.getTimeWindowTo());
    resource
        .add(
            linkTo(controllerProxy.retrieveTrafficStats(timeWindowFrom, timeWindowTo))
                .withSelfRel()
                .withType(GET.name()))
        .add(
            linkTo(controllerProxy.processTrafficEvent(null))
                .withRel("process")
                .withType(PUT.name()));
  }

  @Override
  public void addLinks(CollectionModel<EntityModel<RestTrafficStats>> resources) {}
}
