package com.example.traficprocessor.adapter.presentation.rest.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

import com.example.traficprocessor.adapter.presentation.rest.api.RestTrafficProcessorController;
import com.example.traficprocessor.adapter.presentation.rest.model.RestRecordedTrafficEvent;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;

public class TrafficEventModelAssembler
    implements SimpleRepresentationModelAssembler<RestRecordedTrafficEvent> {
  private static final Class<RestTrafficProcessorController> CONTROLLER =
      RestTrafficProcessorController.class;

  @Override
  public void addLinks(EntityModel<RestRecordedTrafficEvent> resource) {
    var id = resource.getContent().getId();
    var controllerProxy = methodOn(CONTROLLER);
    resource
        .add(linkTo(controllerProxy.retrieveTrafficEvent(id)).withSelfRel().withType(GET.name()))
        .add(
            linkTo(controllerProxy.processTrafficEvent(null))
                .withRel("process")
                .withType(PUT.name()))
        .add(
            linkTo(controllerProxy.retrieveTrafficStats(null, null))
                .withRel("stats")
                .withType(GET.name()));
  }

  @Override
  public void addLinks(CollectionModel<EntityModel<RestRecordedTrafficEvent>> resources) {}
}
