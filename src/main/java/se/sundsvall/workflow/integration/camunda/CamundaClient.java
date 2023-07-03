package se.sundsvall.workflow.integration.camunda;

import generated.se.sundsvall.camunda.ActivityInstanceDto;
import generated.se.sundsvall.camunda.DeploymentDto;
import generated.se.sundsvall.camunda.DeploymentWithDefinitionsDto;
import generated.se.sundsvall.camunda.HistoricExternalTaskLogDto;
import generated.se.sundsvall.camunda.HistoricIncidentDto;
import generated.se.sundsvall.camunda.HistoricProcessInstanceDto;
import generated.se.sundsvall.camunda.ProcessInstanceDto;
import generated.se.sundsvall.camunda.ProcessInstanceWithVariablesDto;
import generated.se.sundsvall.camunda.StartProcessInstanceDto;
import generated.se.sundsvall.camunda.VariableValueDto;
import se.sundsvall.workflow.integration.camunda.configuration.CamundaConfiguration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static se.sundsvall.workflow.integration.camunda.configuration.CamundaConfiguration.CLIENT_ID;

@FeignClient(name = CLIENT_ID, url = "${integration.camunda.url}", configuration = CamundaConfiguration.class)
public interface CamundaClient {

	@PostMapping(path = "process-definition/key/{key}/start", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	ProcessInstanceWithVariablesDto startProcess(@PathVariable("key") String key, StartProcessInstanceDto startProcessInstanceDto);

	@PostMapping(path = "process-definition/key/{key}/tenant-id/{tenant-id}/start", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	ProcessInstanceWithVariablesDto startProcessWithTenant(@PathVariable("key") String key, @PathVariable("tenant-id") String tenantId, StartProcessInstanceDto startProcessInstanceDto);

	@PutMapping(path = "process-instance/{id}/variables/{varName}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	void setProcessInstanceVariable(@PathVariable("id") String id, @PathVariable("varName") String variableName, VariableValueDto variableValueDto);

	@PostMapping(path = "deployment/create", produces = APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE)
	DeploymentWithDefinitionsDto deploy(
		@PathVariable("tenant-id") String tenantId,
		@PathVariable("deployment-source") String deploymentSource,
		@PathVariable("deploy-changed-only") Boolean deployChangedOnly,
		@PathVariable("enable-duplicate-filtering") Boolean enableDuplicateFiltering,
		@PathVariable("deployment-name") String deploymentName,
		@PathVariable("deployment-activation-time")OffsetDateTime deploymentActivationTime,
		@PathVariable("data") File data);

	@GetMapping(path = "deployment", produces = APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE)
	List<DeploymentDto> getDeployments(@RequestParam("source") String source, @RequestParam("name") String name, @RequestParam("tenantIdIn") String tenantIdIn);

	@GetMapping(path = "process-instance/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	ProcessInstanceDto getProcessInstance(@PathVariable("id") String id);

	@GetMapping(path = "process-instance/{id}/activity-instances", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	ActivityInstanceDto getProcessActivityInstance(@PathVariable("id") String id);

	@GetMapping(path = "process-instance/{id}/variables", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	Map<String, VariableValueDto> getProcessInstanceVariables(@PathVariable("id") String id);

	@GetMapping(path =  "history/external-task-log", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	List<HistoricExternalTaskLogDto> getHistoricExternalTaskLog(
		@RequestParam("processInstanceId") String processInstanceId,
		@RequestParam("activityIdIn") String activityIdIn,
		@RequestParam("creationLog") Boolean creationLog,
		@RequestParam("successLog") Boolean successLog,
		@RequestParam("failureLog") Boolean failureLog);

	@GetMapping(path = "history/process-instance/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	HistoricProcessInstanceDto getHistoricProcessInstance(@PathVariable("id") String id);

	@GetMapping(path = "history/incident", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	List<HistoricIncidentDto> getHistoricIncidents(@RequestParam("processInstanceId") String processInstanceId);

}
