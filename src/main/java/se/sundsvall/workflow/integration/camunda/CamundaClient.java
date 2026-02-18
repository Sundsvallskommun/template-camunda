package se.sundsvall.workflow.integration.camunda;

import generated.se.sundsvall.camunda.ActivityInstanceDto;
import generated.se.sundsvall.camunda.DeploymentDto;
import generated.se.sundsvall.camunda.DeploymentWithDefinitionsDto;
import generated.se.sundsvall.camunda.EventSubscriptionDto;
import generated.se.sundsvall.camunda.HistoricActivityInstanceDto;
import generated.se.sundsvall.camunda.HistoricExternalTaskLogDto;
import generated.se.sundsvall.camunda.HistoricIncidentDto;
import generated.se.sundsvall.camunda.HistoricProcessInstanceDto;
import generated.se.sundsvall.camunda.PatchVariablesDto;
import generated.se.sundsvall.camunda.ProcessInstanceDto;
import generated.se.sundsvall.camunda.ProcessInstanceWithVariablesDto;
import generated.se.sundsvall.camunda.StartProcessInstanceDto;
import generated.se.sundsvall.camunda.VariableValueDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.io.File;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.workflow.integration.camunda.configuration.CamundaConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static se.sundsvall.workflow.integration.camunda.configuration.CamundaConfiguration.CLIENT_ID;

@FeignClient(name = CLIENT_ID, url = "${integration.camunda.url}", configuration = CamundaConfiguration.class, dismiss404 = true)
@CircuitBreaker(name = CLIENT_ID)
public interface CamundaClient {

	@PostMapping(path = "process-definition/key/{key}/start", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	ProcessInstanceWithVariablesDto startProcess(@PathVariable("key") final String key, final StartProcessInstanceDto startProcessInstanceDto);

	@PostMapping(path = "process-definition/key/{key}/tenant-id/{tenant-id}/start", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	ProcessInstanceWithVariablesDto startProcessWithTenant(@PathVariable("key") final String key, @PathVariable("tenant-id") final String tenantId, final StartProcessInstanceDto startProcessInstanceDto);

	@PostMapping(path = "process-instance/{id}/variables", consumes = APPLICATION_JSON_VALUE)
	void setProcessInstanceVariables(@PathVariable("id") final String id, final PatchVariablesDto patchVariablesDto);

	@PutMapping(path = "process-instance/{id}/variables/{varName}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	void setProcessInstanceVariable(@PathVariable("id") final String id, @PathVariable("varName") final String variableName, final VariableValueDto variableValueDto);

	@PostMapping(path = "deployment/create", produces = APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE)
	DeploymentWithDefinitionsDto deploy(
		@PathVariable("tenant-id") final String tenantId,
		@PathVariable("deployment-source") final String deploymentSource,
		@PathVariable("deploy-changed-only") final Boolean deployChangedOnly,
		@PathVariable("enable-duplicate-filtering") final Boolean enableDuplicateFiltering,
		@PathVariable("deployment-name") final String deploymentName,
		@PathVariable("deployment-activation-time") final OffsetDateTime deploymentActivationTime,
		@PathVariable("data") final File data);

	@GetMapping(path = "deployment", produces = APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE)
	List<DeploymentDto> getDeployments(@RequestParam("source") final String source, @RequestParam("name") final String name, @RequestParam("tenantIdIn") final String tenantIdIn);

	@GetMapping(path = "process-instance/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	Optional<ProcessInstanceDto> getProcessInstance(@PathVariable("id") final String id);

	@GetMapping(path = "process-instance/{id}/activity-instances", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	ActivityInstanceDto getProcessActivityInstance(@PathVariable("id") final String id);

	@GetMapping(path = "process-instance/{id}/variables", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	Map<String, VariableValueDto> getProcessInstanceVariables(@PathVariable("id") final String id);

	@GetMapping(path = "history/external-task-log", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	List<HistoricExternalTaskLogDto> getHistoricExternalTaskLog(
		@RequestParam("processInstanceId") final String processInstanceId,
		@RequestParam("activityIdIn") final String activityIdIn,
		@RequestParam("creationLog") final Boolean creationLog,
		@RequestParam("successLog") final Boolean successLog,
		@RequestParam("failureLog") final Boolean failureLog);

	@GetMapping(path = "history/process-instance/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	HistoricProcessInstanceDto getHistoricProcessInstance(@PathVariable("id") final String id);

	@GetMapping(path = "history/incident", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	List<HistoricIncidentDto> getHistoricIncidents(@RequestParam("processInstanceId") final String processInstanceId);

	@GetMapping(path = "history/activity-instance", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	List<HistoricActivityInstanceDto> getHistoricActivities(@RequestParam("processInstanceId") String processInstanceId);

	@GetMapping(path = "event-subscription", produces = APPLICATION_JSON_VALUE)
	List<EventSubscriptionDto> getEventSubscriptions();
}
