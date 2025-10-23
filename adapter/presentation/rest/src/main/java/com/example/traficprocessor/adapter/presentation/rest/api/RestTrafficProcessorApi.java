package com.example.traficprocessor.adapter.presentation.rest.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.traficprocessor.adapter.presentation.rest.model.RestRecordedTrafficEvent;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficEvent;
import com.example.traficprocessor.adapter.presentation.rest.model.RestTrafficStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.YearMonth;
import java.util.Optional;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.ResponseEntity;

@Tag(name = "Traffic Processor", description = "Endpoint for processing trafic events and retrieving statistics")
public interface RestTrafficProcessorApi {
  @Operation(summary = "Manually publish TrafficEvent.", responses = {
      @ApiResponse(responseCode = "201", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
      @ApiResponse(responseCode = "401", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "403", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "404", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "500", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
      })
  ResponseEntity<Void> processTrafficEvent(@Parameter(description = "Traffic event to process.", required = true) RestTrafficEvent trafficEvent);
  
  @Operation(summary = "Retrieves TrafficEvent.", responses = {
      @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestRecordedTrafficEvent.class))),
      @ApiResponse(responseCode = "401", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "403", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "404", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "500", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
  })
  ResponseEntity<EntityModel<RestRecordedTrafficEvent>> retrieveTrafficEvent(@Parameter(description = "ID of traffic event.", required = true) String id);
  
  @Operation(summary = "Retrieves TrafficStats for provided time window.", responses = {
      @ApiResponse(responseCode = "204", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestTrafficStats.class))),
      @ApiResponse(responseCode = "401", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "403", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "404", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
      @ApiResponse(responseCode = "500", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
  })
  ResponseEntity<EntityModel<RestTrafficStats>> retrieveTrafficStats(
      @Parameter(description = "Limits traffic events to timestamps greater than or equal timeWindowFrom. Year-Month in the ISO-8601 calendar system, such as 2025-01.", required = true) YearMonth timeWindowFrom,
      @Parameter(description = "Limits traffic events to timestamps less than or equal timeWindowTo. Year-Month in the ISO-8601 calendar system, such as 2025-12. Defaults to timeWindowFrom.plusYears(1).", required = false) Optional<YearMonth> timeWindowTo);
}
