package se.sundsvall.workflow.integration.camunda.worker;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.sundsvall.workflow.integration.camunda.api.CamundaClient;
import se.sundsvall.workflow.integration.camunda.handler.FailureHandler;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component
@ExternalTaskSubscription("checkData")
public class CheckData extends AbstractWorker implements ExternalTaskHandler  {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyWorker.class);

	@Autowired
	private FailureHandler failureHandler;

	@Autowired
	private CamundaClient camundaClient;

	@Override
	public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
		try {
			LOGGER.info("Execute Check data");
			// 1.Clear updateAvailable. This should always be done first
			clearUpdateAvailable(externalTask);


			// 2. Fetch data from datasource. (In this template the source is process variables, but in a real process this would be a integration call)
			Integer updateCounter = externalTask.getVariable("updateCounter");
			LOGGER.info("updateCounter: " + updateCounter);


			// 3. Check if it is ok to move to the next task (no data is missing).
			Map<String, Object> processVariables = new HashMap<>();
			if(externalTask.getBusinessKey().equals("will_need_two_updates")){
				if(updateCounter == null) {
					updateCounter = 1;
				} else {
					updateCounter = updateCounter + 1;
				}

				if(updateCounter <= 2) { // data is missing
					LOGGER.info("More updates will be needed");
					processVariables.put("dataMissing", TRUE);
				} else {
					LOGGER.info("Updates done");
					processVariables.put("dataMissing", FALSE);
				}
			} else {
				processVariables.put("dataMissing", FALSE);
			}


			// 4. Complete
			processVariables.put("updateCounter", updateCounter);
			externalTaskService.complete(externalTask, processVariables);
		} catch (Exception exception) {
			LOGGER.error("Exception occurred in execution for task with id {} and businesskey {}", externalTask.getId(), externalTask.getBusinessKey());

			failureHandler.handleException(externalTaskService, externalTask, exception.getMessage());
		}
	}
}
