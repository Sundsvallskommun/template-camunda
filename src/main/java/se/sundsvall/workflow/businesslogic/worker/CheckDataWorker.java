package se.sundsvall.workflow.businesslogic.worker;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.sundsvall.workflow.businesslogic.handler.FailureHandler;
import se.sundsvall.workflow.integration.camunda.CamundaClient;

@Component
@ExternalTaskSubscription("checkData")
public class CheckDataWorker extends AbstractWorker implements ExternalTaskHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckDataWorker.class);
	private static final String DATA_MISSING = "dataMissing";

	private final FailureHandler failureHandler;

	public CheckDataWorker(FailureHandler failureHandler, CamundaClient camundaClient) {
		super(camundaClient);
		this.failureHandler = failureHandler;
	}

	@Override
	public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
		try {
			LOGGER.info("Execute Check data");

			// 1.Clear updateAvailable. This should always be done first
			clearUpdateAvailable(externalTask);

			// 2. Fetch data from datasource. (In this template the source is process variables, but in a real process this would be
			// a integration call)
			Integer updateCounter = externalTask.getVariable("updateCounter");
			LOGGER.info("updateCounter: {}", updateCounter);

			// 3. Check if it is ok to move to the next task (no data is missing).
			final Map<String, Object> processVariables = new HashMap<>();
			if ("will_need_two_updates".equals(externalTask.getBusinessKey())) {
				if (updateCounter == null) {
					updateCounter = 1;
				} else {
					updateCounter = updateCounter + 1;
				}

				if (updateCounter <= 2) { // data is missing
					LOGGER.info("More updates will be needed");
					processVariables.put(DATA_MISSING, TRUE);
				} else {
					LOGGER.info("Updates done");
					processVariables.put(DATA_MISSING, FALSE);
				}
			} else {
				processVariables.put(DATA_MISSING, FALSE);
			}

			// 4. Complete
			processVariables.put("updateCounter", updateCounter);
			externalTaskService.complete(externalTask, processVariables);
		} catch (final Exception exception) {
			LOGGER.error("Exception occurred in execution for task with id {} and businesskey {}", externalTask.getId(), externalTask.getBusinessKey());

			failureHandler.handleException(externalTaskService, externalTask, exception.getMessage());
		}
	}
}
