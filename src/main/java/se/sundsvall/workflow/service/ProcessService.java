package se.sundsvall.workflow.service;


import static se.sundsvall.workflow.Constants.PROCESS_KEY;

import generated.se.sundsvall.camunda.StartProcessInstanceDto;
import generated.se.sundsvall.camunda.VariableValueDto;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.sundsvall.workflow.integration.camunda.api.CamundaClient;


@Service
public class ProcessService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);

	private static final String TENANT_ID_TEMPLATE = "TEMPLATE_NAMESPACE";

	@Autowired
	CamundaClient client;

	public String startProcess(String businessKey, String tenantId) {
		return client.startProcessWithTenant(PROCESS_KEY, TENANT_ID_TEMPLATE, new StartProcessInstanceDto().businessKey(businessKey)).getId();
	}

	public void updateProcess(String processInstanceId) {
		client.setProcessInstanceVariable(processInstanceId, "updateAvailable", new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(true));
	}
}
