package se.sundsvall.workflow.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.workflow.Application;
import se.sundsvall.workflow.api.model.StartProcessResponse;
import se.sundsvall.workflow.service.ProcessService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class ProcessResourceTest {

	private static final String START_PATH = "/{municipalityId}/{namespace}/process/start/{businessKey}";
	private static final String UPDATE_PATH = "/{municipalityId}/{namespace}/process/update/{processInstanceId}";

	@MockitoBean
	private ProcessService processServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@LocalServerPort
	private int port;

	@Test
	void startProcess() {

		// Arrange
		final var municipalityId = "2281";
		final var namespace = "TEMPLATE_NAMESPACE";
		final var businessKey = "businessKey";
		final var processInstanceId = randomUUID().toString();

		when(processServiceMock.startProcess(municipalityId, namespace, businessKey)).thenReturn(processInstanceId);

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(START_PATH)
				.build(Map.of("municipalityId", municipalityId, "namespace", namespace, "businessKey", businessKey)))
			.exchange()
			.expectStatus().isAccepted()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(StartProcessResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getProcessId()).isEqualTo(processInstanceId);
		verify(processServiceMock).startProcess(municipalityId, namespace, businessKey);
		verifyNoMoreInteractions(processServiceMock);
	}

	@Test
	void updateProcess() {

		// Arrange
		final var municipalityId = "2281";
		final var namespace = "TEMPLATE_NAMESPACE";
		final var businessKey = "businessKey";
		final var processInstanceId = randomUUID().toString();

		when(processServiceMock.startProcess(municipalityId, namespace, businessKey)).thenReturn(processInstanceId);

		// Act
		webTestClient.post()
			.uri(builder -> builder.path(UPDATE_PATH)
				.build(Map.of("municipalityId", municipalityId, "namespace", namespace, "processInstanceId", processInstanceId)))
			.exchange()
			.expectStatus().isAccepted()
			.expectBody().isEmpty();

		// Assert
		verify(processServiceMock).updateProcess(municipalityId, namespace, processInstanceId);
		verifyNoMoreInteractions(processServiceMock);
	}
}
