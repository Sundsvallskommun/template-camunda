package se.sundsvall.workflow.businesslogic.worker;

import org.camunda.bpm.client.task.ExternalTask;
import se.sundsvall.workflow.integration.camunda.CamundaClient;

import static se.sundsvall.workflow.Constants.FALSE;
import static se.sundsvall.workflow.Constants.UPDATE_AVAILABLE;

abstract class AbstractWorker {

	private final CamundaClient camundaClient;

	protected AbstractWorker(final CamundaClient camundaClient) {
		this.camundaClient = camundaClient;
	}

	protected void clearUpdateAvailable(final ExternalTask externalTask) {
		/*
		 * Clearing process variable has to be a blocking operation.
		 * Using ExternalTaskService.setVariables() will not work without creating race conditions.
		 */
		camundaClient.setProcessInstanceVariable(externalTask.getProcessInstanceId(), UPDATE_AVAILABLE, FALSE);
	}
}
