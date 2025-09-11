package se.sundsvall.workflow.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.workflow.Constants.CAMUNDA_VARIABLE_MUNICIPALITY_ID;
import static se.sundsvall.workflow.Constants.CAMUNDA_VARIABLE_NAMESPACE;
import static se.sundsvall.workflow.Constants.CAMUNDA_VARIABLE_REQUEST_ID;
import static se.sundsvall.workflow.Constants.CAMUNDA_VARIABLE_UPDATE_AVAILABLE;
import static se.sundsvall.workflow.Constants.PROCESS_KEY;
import static se.sundsvall.workflow.Constants.TENANTID_TEMPLATE;
import static se.sundsvall.workflow.Constants.TRUE;
import static se.sundsvall.workflow.integration.camunda.mapper.CamundaMapper.toPatchVariablesDto;
import static se.sundsvall.workflow.integration.camunda.mapper.CamundaMapper.toStartProcessInstanceDto;
import static se.sundsvall.workflow.integration.camunda.mapper.CamundaMapper.toVariableValueDto;

import java.util.Map;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.dept44.requestid.RequestId;
import se.sundsvall.workflow.integration.camunda.CamundaClient;

@Service
public class ProcessService {

	private final CamundaClient camundaClient;

	public ProcessService(final CamundaClient camundaClient) {
		this.camundaClient = camundaClient;
	}

	public String startProcess(final String municipalityId, final String namespace, final String businessKey) {

		return camundaClient.startProcessWithTenant(PROCESS_KEY, TENANTID_TEMPLATE,
			toStartProcessInstanceDto(municipalityId, namespace, businessKey)).getId();
	}

	public void updateProcess(final String municipalityId, final String namespace, final String processInstanceId) {

		verifyExistingProcessInstance(processInstanceId);

		final var variablesToUpdate = Map.of(
			CAMUNDA_VARIABLE_MUNICIPALITY_ID, toVariableValueDto(ValueType.STRING, municipalityId),
			CAMUNDA_VARIABLE_NAMESPACE, toVariableValueDto(ValueType.STRING, namespace),
			CAMUNDA_VARIABLE_UPDATE_AVAILABLE, TRUE,
			CAMUNDA_VARIABLE_REQUEST_ID, toVariableValueDto(ValueType.STRING, RequestId.get()));

		camundaClient.setProcessInstanceVariables(processInstanceId, toPatchVariablesDto(variablesToUpdate));
	}

	private void verifyExistingProcessInstance(final String processInstanceId) {
		if (camundaClient.getProcessInstance(processInstanceId).isEmpty()) {
			throw Problem.valueOf(NOT_FOUND, "Process instance with ID '%s' does not exist!".formatted(processInstanceId));
		}
	}
}
