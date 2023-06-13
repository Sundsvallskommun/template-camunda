package se.sundsvall.workflow.integration.camunda.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;

import se.sundsvall.workflow.integration.camunda.deployment.DeploymentProperties.ProcessArchive;

class DeploymentPropertiesTest {

	@Test
	void testAnnotation() {
		assertThat(DeploymentProperties.class).hasAnnotation(ConfigurationProperties.class);
		assertThat(DeploymentProperties.class.getAnnotation(ConfigurationProperties.class).value()).isEqualTo("camunda.bpm.deployment");
	}

	@Test
	void emptyDeploymentProperties() {
		DeploymentProperties props = new DeploymentProperties();

		assertThat(props.getProcesses())
			.isNull();
	}

	@Test
	void withDeploymentProperties() {
		final var autoDeploy = false;
		final var name = "name";
		final var tenant = "tenant";
		final var bpmnResources = "bpmnResources";
		final var dmnResources = "dmnResources";
		final var formResources = "formResources";
		final var processArchive = new ProcessArchive(name, tenant, bpmnResources, dmnResources, formResources);

		DeploymentProperties props = new DeploymentProperties();
		props.setAutoDeployEnabled(autoDeploy);
		props.setProcesses(List.of(processArchive));

		assertThat(props.isAutoDeployEnabled()).isFalse();
		assertThat(props.getProcesses())
			.isNotNull()
			.containsExactly(processArchive);
	}

	@Test
	void emptyProcessArchive() {
		assertThat(new ProcessArchive(null, null, null, null, null)).hasAllNullFieldsOrProperties();
	}
}
