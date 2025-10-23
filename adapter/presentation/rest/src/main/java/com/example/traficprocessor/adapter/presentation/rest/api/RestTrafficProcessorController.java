package com.example.traficprocessor.adapter.presentation.rest.api;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_API_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_EVENTS_RESOURCE_PART;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.TRAFFIC_STATS_PART;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.traficprocessor.adapter.presentation.rest.hateoas.TrafficEventModelAssembler;
import com.example.traficprocessor.adapter.presentation.rest.hateoas.TrafficStatsModelAssembler;
import com.example.traficprocessor.adapter.presentation.rest.model.RestRecordedTrafficEvent;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficEvent;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficStats;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTraficStatsFactory;
import com.example.traficprocessor.core.domain.TrafficProcessorService;
import java.time.YearMonth;
import java.util.Optional;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(TRAFFIC_EVENTS_API_PATH)
public class RestTrafficProcessorController implements RestTrafficProcessorApi {
  private final TrafficProcessorService trafficProcessorService;
  private final TrafficEventModelAssembler trafficEventModelAssembler =
      new TrafficEventModelAssembler();
  private final TrafficStatsModelAssembler trafficStatsModelAssembler =
      new TrafficStatsModelAssembler();

  public RestTrafficProcessorController(TrafficProcessorService trafficProcessorService) {
    this.trafficProcessorService = trafficProcessorService;
  }

  @Override
  @PostMapping
  public ResponseEntity<Void> processTrafficEvent(@RequestBody RestTrafficEvent trafficEvent) {
    var id = trafficProcessorService.processTrafficEvent(trafficEvent);
    var location = linkTo(methodOn(getClass()).retrieveTrafficEvent(id)).toUri();
    return ResponseEntity.created(location).build();
  }

  @Override
  @GetMapping(value = TRAFFIC_EVENTS_RESOURCE_PART, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<EntityModel<RestRecordedTrafficEvent>> retrieveTrafficEvent(
      @PathVariable String id) {
    var event = trafficProcessorService.retrieveTrafficEvent(id, RestRecordedTrafficEvent::new);
    return ResponseEntity.ok(trafficEventModelAssembler.toModel(event));
  }

  @Override
  @GetMapping(value = TRAFFIC_STATS_PART, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<EntityModel<RestTrafficStats>> retrieveTrafficStats(
      @RequestParam YearMonth timeWindowFrom,
      @RequestParam(required = false) Optional<YearMonth> timeWindowTo) {
    var stats =
        trafficProcessorService.retrieveTrafficStats(
            timeWindowFrom,
            timeWindowTo.orElseGet(() -> timeWindowFrom.plusYears(1)),
            RestTraficStatsFactory.INSTANCE);
    return ResponseEntity.ok(trafficStatsModelAssembler.toModel(stats));
  }
}
