package se.sundsvall.workflow.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.workflow.api.model.StartProcessResponse;
import se.sundsvall.workflow.service.ProcessService;

@RestController
@RequestMapping("process")
@Tag(name = "Camunda process endpoints", description = "Endpoints for starting and updating camunda processes")
public class ProcessResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessResource.class);

	@Autowired
	private ProcessService service;

	@PostMapping(path = "start/{businessKey}", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(description = "Start a new process instance for the provided business key")
	@ApiResponse(responseCode = "202", description = "Accepted", content = @Content(schema = @Schema(implementation = StartProcessResponse.class)))
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<StartProcessResponse> startProcess(
		@Parameter(name = "businessKey") @PathVariable final String businessKey) {

		var startProcessResponse = new StartProcessResponse(service.startProcess(businessKey));
		LOGGER.info("Request for start of process has been received, resulting in an instance with id {}", startProcessResponse.getProcessId());
		return new ResponseEntity<>(startProcessResponse, HttpStatus.ACCEPTED);
	}

	@PostMapping(path = "update/{processInstanceId}", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(description = "Update a process instance matching the provided processInstanceId")
	@ApiResponse(responseCode = "202", description = "Accepted", content = @Content(schema = @Schema(implementation = Void.class)))
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> updateProcess(
		@Parameter(name = "processInstanceId") @PathVariable final String processInstanceId) {

		LOGGER.info("Request for update of process instance with id {} has been received", processInstanceId);
		service.updateProcess(processInstanceId);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

}
