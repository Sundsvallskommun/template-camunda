package apptest;

import static generated.se.sundsvall.camunda.HistoricProcessInstanceDto.StateEnum.ACTIVE;
import static generated.se.sundsvall.camunda.HistoricProcessInstanceDto.StateEnum.COMPLETED;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.setDefaultPollDelay;
import static org.awaitility.Awaitility.setDefaultPollInterval;
import static org.awaitility.Awaitility.setDefaultTimeout;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.JsonProcessingException;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.workflow.Application;
import se.sundsvall.workflow.api.model.StartProcessResponse;
import se.sundsvall.workflow.integration.camunda.CamundaClient;

/**
 * Test class using testcontainer to execute the process.
 * There are a lot of resources that can be added to CamundaClient
 * to make good assertions. This test class contains a few examples.
 * See Camunda API for more details https://docs.camunda.org/rest/camunda-bpm-platform/7.19/
 */
@WireMockAppTestSuite(files = "classpath:/CreateProcess/", classes = Application.class)
@Testcontainers
public class CreateProcessIT extends AbstractAppTest {

	@Container
	private static final GenericContainer<?> CAMUNDA =
		new GenericContainer<>("camunda/camunda-bpm-platform:run-7.19.0")
			.waitingFor(Wait.forHttp("/"))
			.withExposedPorts(8080);

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		CAMUNDA.start();
		var camundaBaseUrl = new String("http://" + "localhost:" + CAMUNDA.getMappedPort(8080) + "/engine-rest");
		registry.add("integration.camunda.url", () -> camundaBaseUrl);
		registry.add("camunda.bpm.client.base-url", () -> camundaBaseUrl);
	}

	@Autowired
	private CamundaClient camundaClient;

	@BeforeEach
	void setup() {
		setDefaultPollInterval(500, MILLISECONDS);
		setDefaultPollDelay(Duration.ZERO);
		setDefaultTimeout(Duration.ofSeconds(30));

		await()
			.ignoreExceptions()
			.until(() -> camundaClient.getDeployments("template-process.bpmn", null, null).size(), equalTo(1));
	}

	@AfterAll
	static void teardown() {
		CAMUNDA.stop();
	}

	@Test
	void test001_createProcessWithoutUpdates() throws JsonProcessingException, ClassNotFoundException {

		// === Start process ===
		final var startResponse = setupCall()
			.withServicePath("/process/start/businessKey")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.sendRequestAndVerifyResponse()
			.andReturnBody(StartProcessResponse.class);

		await()
			.until(() -> camundaClient.getHistoricProcessInstance(startResponse.getProcessId()).getState(), equalTo(COMPLETED));

		// ExternalTask_MyWorker has been executed 1 time
		assertThat(camundaClient.getHistoricExternalTaskLog(startResponse.getProcessId(), "ExternalTask_MyWorker", false, true, false)).hasSize(1);
		// ExternalTask_CheckData has been executed 1 time
		assertThat(camundaClient.getHistoricExternalTaskLog(startResponse.getProcessId(), "ExternalTask_CheckData", false, true, false)).hasSize(1);
	}

	@Test
	void test002_createProcessWithTwoUpdates() throws JsonProcessingException, ClassNotFoundException {

		// === Start process ===
		final var startResponse = setupCall()
			.withServicePath("/process/start/will_need_two_updates")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.sendRequestAndVerifyResponse()
			.andReturnBody(StartProcessResponse.class);

		// Wait for process to start
		await()
			.ignoreExceptions()
			.until(() -> camundaClient.getHistoricProcessInstance(startResponse.getProcessId()).getState(), equalTo(ACTIVE));

		// Wait for process to be in state "Update Available?"
		await()
			.until(() -> camundaClient.getProcessActivityInstance(startResponse.getProcessId()).getChildActivityInstances().get(0).getActivityName(), equalTo("Update Available?"));

		assertThat(camundaClient.getHistoricExternalTaskLog(startResponse.getProcessId(), "ExternalTask_CheckData", false, true, false)).hasSize(1);

		// === Update process first time ===
		setupCall()
			.withServicePath("/process/update/" + startResponse.getProcessId())
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.sendRequestAndVerifyResponse();

		// Wait for process to execute worker
		await()
			.atMost(30, SECONDS)
			.until(() -> camundaClient.getProcessInstanceVariables(startResponse.getProcessId()).get("updateAvailable").getValue(), equalTo(false));
		// Wait for process to be in state "Update Available?"
		await()
			.atMost(30, SECONDS)
			.until(() -> camundaClient.getProcessActivityInstance(startResponse.getProcessId()).getChildActivityInstances().get(0).getActivityName(), equalTo("Update Available?"));

		assertThat(camundaClient.getHistoricExternalTaskLog(startResponse.getProcessId(), "ExternalTask_CheckData", false, true, false)).hasSize(2);

		// === Update process second time ===
		setupCall()
			.withServicePath("/process/update/" + startResponse.getProcessId())
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.sendRequestAndVerifyResponse();

		// Wait for process to complete
		await()
			.atMost(30, SECONDS)
			.until(() -> camundaClient.getHistoricProcessInstance(startResponse.getProcessId()).getState(), equalTo(COMPLETED));


		// ExternalTask_MyWorker has been executed 1 time
		assertThat(camundaClient.getHistoricExternalTaskLog(startResponse.getProcessId(), "ExternalTask_MyWorker", false, true, false)).hasSize(1);
		// ExternalTask_CheckData has been executed 3 time
		assertThat(camundaClient.getHistoricExternalTaskLog(startResponse.getProcessId(), "ExternalTask_CheckData", false, true, false)).hasSize(3);

	}

	@Test
	void test003_testMyWorkerThrowsException()  throws JsonProcessingException, ClassNotFoundException {
		// === Start process ===
		final var startResponse = setupCall()
			.withServicePath("/process/start/throw_exception")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.sendRequestAndVerifyResponse()
			.andReturnBody(StartProcessResponse.class);

		// Wait for 4 failure (1 ordinary and 3 retries)
		await()
			.atMost(30, SECONDS)
			.ignoreExceptions()
			.until(() -> camundaClient.getHistoricExternalTaskLog(startResponse.getProcessId(), "ExternalTask_MyWorker", false, false, true).size(), equalTo(4));

		var incidents = camundaClient.getHistoricIncidents(startResponse.getProcessId());
		assertThat(incidents).hasSize(1);
		assertThat(incidents.get(0).getIncidentType()).isEqualTo("failedExternalTask");
		assertThat(incidents.get(0).getActivityId()).isEqualTo("ExternalTask_MyWorker");
	}

}
