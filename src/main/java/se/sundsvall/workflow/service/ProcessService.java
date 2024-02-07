package se.sundsvall.workflow.service;

import static se.sundsvall.workflow.Constants.PROCESS_KEY;
import static se.sundsvall.workflow.Constants.TENANTID_TEMPLATE;
import static se.sundsvall.workflow.Constants.TRUE;
import static se.sundsvall.workflow.Constants.UPDATE_AVAILABLE;

import org.springframework.stereotype.Service;

import generated.se.sundsvall.camunda.StartProcessInstanceDto;
import se.sundsvall.workflow.integration.camunda.CamundaClient;

@Service
public class ProcessService {

	private final CamundaClient camundaClient;

	public ProcessService(CamundaClient camundaClient) {
		this.camundaClient = camundaClient;
	}

	public String startProcess(String businessKey) {
		return camundaClient.startProcessWithTenant(PROCESS_KEY, TENANTID_TEMPLATE, new StartProcessInstanceDto().businessKey(businessKey)).getId();
	}

	public void updateProcess(String processInstanceId) {
		camundaClient.setProcessInstanceVariable(processInstanceId, UPDATE_AVAILABLE, TRUE);
	}
}
