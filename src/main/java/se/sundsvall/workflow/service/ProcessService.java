package se.sundsvall.workflow.service;

import static se.sundsvall.workflow.Constants.PROCESS_KEY;
import static se.sundsvall.workflow.integration.camunda.mapper.CamundaMapper.toCorrelationMessageDto;
import static se.sundsvall.workflow.integration.camunda.mapper.CamundaMapper.toStartProcessInstanceDto;

import org.camunda.community.rest.client.api.MessageApi;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Service
public class ProcessService {

	@Autowired
	private MessageApi messageApi;

	@Autowired
	private ProcessDefinitionApi processDefinitionApi;

	public String startProcess(String businessKey, String tenantId) {
		try {
			return processDefinitionApi.startProcessInstanceByKeyAndTenantId(PROCESS_KEY, tenantId, toStartProcessInstanceDto(businessKey)).getId();
		} catch (ApiException e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	public void updateProcess(String businessKey, String tenantId, String phase) {
		try {
			messageApi.deliverMessage(toCorrelationMessageDto(businessKey, tenantId, phase));
		} catch (ApiException e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
