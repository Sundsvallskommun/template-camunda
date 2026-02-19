package se.sundsvall.workflow.service;

import generated.se.sundsvall.camunda.PatchVariablesDto;
import generated.se.sundsvall.camunda.ProcessInstanceDto;
import generated.se.sundsvall.camunda.ProcessInstanceWithVariablesDto;
import generated.se.sundsvall.camunda.StartProcessInstanceDto;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.workflow.integration.camunda.CamundaClient;

import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

	@Mock
	private CamundaClient camundaClientMock;

	@InjectMocks
	private ProcessService processService;

	@Test
	void startProcess() {

		// Arrange
		final var process = "template-camunda-process";
		final var tenant = "TEMPLATE_NAMESPACE";
		final var municipalityId = "municipalityId";
		final var namespace = "TEMPLATE_NAMESPACE";
		final var businessKey = RandomStringUtils.secure().next(10);
		final var uuid = randomUUID().toString();
		final var processInstance = new ProcessInstanceWithVariablesDto().id(uuid);

		when(camundaClientMock.startProcessWithTenant(any(), any(), any())).thenReturn(processInstance);

		// Act
		final var processId = processService.startProcess(municipalityId, namespace, businessKey);

		// Assert
		assertThat(processId).isEqualTo(uuid);
		verify(camundaClientMock).startProcessWithTenant(eq(process), eq(tenant), any(StartProcessInstanceDto.class));
		verifyNoMoreInteractions(camundaClientMock);
	}

	@Test
	void updateProcess() {

		// Arrange
		final var municipalityId = "municipalityId";
		final var namespace = "TEMPLATE_NAMESPACE";
		final var uuid = randomUUID().toString();

		when(camundaClientMock.getProcessInstance(any())).thenReturn(Optional.of(new ProcessInstanceDto()));

		// Act
		processService.updateProcess(municipalityId, namespace, uuid);

		// Assert
		verify(camundaClientMock).getProcessInstance(uuid);
		verify(camundaClientMock).setProcessInstanceVariables(eq(uuid), any(PatchVariablesDto.class));
		verifyNoMoreInteractions(camundaClientMock);
	}

	@Test
	void updateProcessNotFound() {

		// Arrange
		final var municipalityId = "municipalityId";
		final var namespace = "TEMPLATE_NAMESPACE";
		final var uuid = randomUUID().toString();

		when(camundaClientMock.getProcessInstance(any())).thenReturn(empty());

		// Act
		final var result = assertThrows(ThrowableProblem.class, () -> processService.updateProcess(municipalityId, namespace, uuid));

		// Assert
		assertThat(result)
			.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND)
			.hasFieldOrPropertyWithValue("detail", "Process instance with ID '%s' does not exist!".formatted(uuid));

		verify(camundaClientMock).getProcessInstance(uuid);
		verify(camundaClientMock, never()).setProcessInstanceVariable(any(), any(), any());
		verifyNoMoreInteractions(camundaClientMock);
	}
}
