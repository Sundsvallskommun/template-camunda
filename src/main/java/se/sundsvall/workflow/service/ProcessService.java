package se.sundsvall.workflow.service;

import static java.lang.Boolean.FALSE;
import static se.sundsvall.workflow.Constants.PROCESS_KEY;
//import static se.sundsvall.workflow.integration.camunda.mapper.CamundaMapper.toCorrelationMessageDto;
//import static se.sundsvall.workflow.integration.camunda.mapper.CamundaMapper.toStartProcessInstanceDto;

import generated.se.sundsvall.camunda.StartProcessInstanceDto;
import generated.se.sundsvall.camunda.VariableValueDto;
import org.camunda.bpm.engine.RuntimeService;
/*
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.community.rest.client.api.MessageApi;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.api.ProcessInstanceApi;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;
import org.camunda.community.rest.client.api.ProcessDefinitionApiClient;
*/
import org.camunda.bpm.engine.variable.type.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.workflow.integration.camunda.api.CamundaClient;

import java.util.Map;

@Service
public class ProcessService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);

	/*@Autowired
	private MessageApi messageApi;

	@Autowired
	private ProcessDefinitionApi processDefinitionApi;

	@Autowired
	private ProcessInstanceApi processInstanceApi;

	public String startProcess(String businessKey, String tenantId) {
		try {
			return processDefinitionApi.startProcessInstanceByKeyAndTenantId(PROCESS_KEY, tenantId, toStartProcessInstanceDto(businessKey)).getId();
		} catch (ApiException e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	public void updateProcess(String processInstanceId) {
		try {
			processInstanceApi.setProcessInstanceVariable(processInstanceId, "updateAvailable", new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(true));
		} catch (ApiException e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	 */

	private static final String TENANTID_TEMPLATE = "TEMPLATE_NAMESPACE";

	@Autowired
	CamundaClient client;

	public String startProcess(String businessKey, String tenantId) {
		return client.startProcessWithTenant(PROCESS_KEY, TENANTID_TEMPLATE, new StartProcessInstanceDto().businessKey(businessKey)).getId();
	}

	public void updateProcess(String processInstanceId) {
		client.setProcessInstanceVariable(processInstanceId, "updateAvailable", new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(true));
	}
}
