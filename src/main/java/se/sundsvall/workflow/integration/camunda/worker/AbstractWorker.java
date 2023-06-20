package se.sundsvall.workflow.integration.camunda.worker;

import generated.se.sundsvall.camunda.VariableValueDto;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.springframework.beans.factory.annotation.Autowired;
import se.sundsvall.workflow.integration.camunda.api.CamundaClient;

public class AbstractWorker {

	@Autowired
	private CamundaClient camundaClient;

	protected void clearUpdateAvailable(ExternalTask externalTask) {
		/* Clearing process variable has to be a blocking operation.
		 * Using ExternalTaskService.setVariables() will not work without creating race conditions.
		 */
		camundaClient.setProcessInstanceVariable(externalTask.getProcessInstanceId(), "updateAvailable", new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(false));
	}
}
