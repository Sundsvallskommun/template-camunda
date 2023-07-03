package se.sundsvall.workflow.integration.camunda.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.camunda")
public record CamundaProperties(int connectTimeout, int readTimeout) {
}
