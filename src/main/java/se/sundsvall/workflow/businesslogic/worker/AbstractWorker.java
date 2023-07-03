package se.sundsvall.workflow.businesslogic.worker;

import static se.sundsvall.workflow.Constants.FALSE;
import static se.sundsvall.workflow.Constants.UPDATE_AVAILABLE;

import org.camunda.bpm.client.task.ExternalTask;
import org.springframework.beans.factory.annotation.Autowired;

import se.sundsvall.workflow.integration.camunda.CamundaClient;

abstract class AbstractWorker {
	@Autowired
	private CamundaClient camundaClient;

	protected void clearUpdateAvailable(ExternalTask externalTask) {
		/* Clearing process variable has to be a blocking operation.
		 * Using ExternalTaskService.setVariables() will not work without creating race conditions.
		 */
		camundaClient.setProcessInstanceVariable(externalTask.getProcessInstanceId(), UPDATE_AVAILABLE, FALSE);
	}
}
