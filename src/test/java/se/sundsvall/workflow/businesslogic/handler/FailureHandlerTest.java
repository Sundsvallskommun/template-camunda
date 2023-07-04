package se.sundsvall.workflow.businesslogic.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.workflow.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class FailureHandlerTest {

	@Autowired
	private FailureHandler failureHandler;

	@MockBean
	private ExternalTaskService externalTaskServiceMock;

	@MockBean
	private ExternalTask externalTaskMock;

	@Test
	void handlehandleExceptionWithVariables() {
		// Setup
		final var message = "message";
		final var id = UUID.randomUUID().toString();
		final var workerId = UUID.randomUUID().toString();
		final var retriesLeft = 2;
		final Map<String, Object> variables = Map.of("key", "value");

		// Mock
		when(externalTaskMock.getId()).thenReturn(id);
		when(externalTaskMock.getWorkerId()).thenReturn(workerId);
		when(externalTaskMock.getRetries()).thenReturn(retriesLeft);

		// Act
		failureHandler.handleException(externalTaskServiceMock, externalTaskMock, message, variables);

		// Assert and verify
		verify(externalTaskMock).getId();
		verify(externalTaskMock).getWorkerId();
		verify(externalTaskServiceMock).handleFailure(id, workerId, message, retriesLeft - 1, 10, variables, Collections.emptyMap());
		verifyNoMoreInteractions(externalTaskServiceMock);
	}

	@Test
	void handlehandleExceptionWithoutVariables() {
		// Setup
		final var message = "message";
		final var id = UUID.randomUUID().toString();
		final var workerId = UUID.randomUUID().toString();
		final var retriesLeft = 2;

		// Mock
		when(externalTaskMock.getId()).thenReturn(id);
		when(externalTaskMock.getWorkerId()).thenReturn(workerId);
		when(externalTaskMock.getRetries()).thenReturn(retriesLeft);

		// Act
		failureHandler.handleException(externalTaskServiceMock, externalTaskMock, message);

		// Assert and verify
		verify(externalTaskMock).getId();
		verify(externalTaskMock).getWorkerId();
		verify(externalTaskServiceMock).handleFailure(id, workerId, message, retriesLeft - 1, 10);
		verifyNoMoreInteractions(externalTaskServiceMock);
	}

	@Test
	void handlehandleExceptionWhenRetriesNotSet() {
		// Setup
		final var message = "message";
		final var id = UUID.randomUUID().toString();
		final var workerId = UUID.randomUUID().toString();

		// Mock
		when(externalTaskMock.getId()).thenReturn(id);
		when(externalTaskMock.getWorkerId()).thenReturn(workerId);
		when(externalTaskMock.getRetries()).thenReturn(null);

		// Act
		failureHandler.handleException(externalTaskServiceMock, externalTaskMock, message);

		// Assert and verify
		verify(externalTaskMock).getId();
		verify(externalTaskMock).getWorkerId();
		verify(externalTaskServiceMock).handleFailure(id, workerId, message, 3, 10);
		verifyNoMoreInteractions(externalTaskServiceMock);
	}
}
