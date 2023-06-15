package se.sundsvall.workflow.integration.camunda.worker;

import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.workflow.integration.camunda.handler.FailureHandler;

@Component
@ExternalTaskSubscription("printMessage")
public class PrintMessageWorker implements ExternalTaskHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PrintMessageWorker.class);

	@Autowired
	private FailureHandler failureHandler;

	@Override
	public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
		try {

			if (RandomUtils.nextBoolean()) {
				throw Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout");
			}
			LOGGER.info("Finally made it, printing some message for task with id {} and businesskey {}", externalTask.getId(), externalTask.getBusinessKey());

			externalTaskService.complete(externalTask, Map.of("phase", "printMessage"));
		} catch (Exception exception) {
			LOGGER.error("Exception occured in execution of print message for task with id {} and businesskey {}", externalTask.getId(), externalTask.getBusinessKey());

			failureHandler.handleException(externalTaskService, externalTask, exception.getMessage());
		}
	}
}
