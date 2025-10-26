package com.example.traficprocessor.adapter.presentation.rest.hateoas;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.API_PATH;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.example.traficprocessor.adapter.presentation.rest.api.RestTrafficProcessorController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_PATH)
public class ModelRepresentationController {
  @GetMapping(produces = HAL_JSON_VALUE)
  ResponseEntity<RepresentationModel<?>> modelRepresentation() {
    var model = new RepresentationModel<>();
    model.add(
        linkTo(methodOn(ModelRepresentationController.class).modelRepresentation()).withSelfRel());

    var controller = RestTrafficProcessorController.class;
    model.add(
        linkTo(methodOn(controller).processTrafficEvent(null))
            .withRel("processTrafficEvent")
            .withType(POST.name()));
    model.add(
        linkTo(methodOn(controller).retrieveTrafficEvent(null))
            .withRel("retrieveTrafficEvent")
            .withType(GET.name()));
    model.add(
        linkTo(methodOn(controller).retrieveTrafficStats(null, null))
            .withRel("retrieveTrafficStats")
            .withType(GET.name()));

    return ResponseEntity.ok(model);
  }
}
