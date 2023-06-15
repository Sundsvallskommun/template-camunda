package se.sundsvall.workflow.integration.camunda.mapper;

import java.util.Map;

import org.camunda.community.rest.client.dto.CorrelationMessageDto;
import org.camunda.community.rest.client.dto.StartProcessInstanceDto;
import org.camunda.community.rest.client.dto.VariableValueDto;

public class CamundaMapper {
	private static final String KEY_PROCESS_ID = "processId";

	private CamundaMapper() {}

	public static StartProcessInstanceDto toStartProcessInstanceDto(String businessKey) {
		return new StartProcessInstanceDto()
			.businessKey(businessKey)
			.variables(Map.of(KEY_PROCESS_ID, new VariableValueDto()
				.type(String.class.getSimpleName())
				.value(businessKey)));
	}

	public static CorrelationMessageDto toCorrelationMessageDto(String businessKey, String tenantId, String phase) {
		return new CorrelationMessageDto()
			.messageName(phase.toLowerCase())
			.businessKey(businessKey)
			.tenantId(tenantId)
			.processVariables(Map.of(KEY_PROCESS_ID, new VariableValueDto()
				.type(String.class.getSimpleName())
				.value(businessKey)));
	}
}
