package se.sundsvall.workflow.integration.camunda.mapper;

import generated.se.sundsvall.camunda.PatchVariablesDto;
import generated.se.sundsvall.camunda.StartProcessInstanceDto;
import generated.se.sundsvall.camunda.VariableValueDto;
import java.util.Map;
import org.camunda.bpm.engine.variable.type.ValueType;
import se.sundsvall.dept44.requestid.RequestId;

import static se.sundsvall.workflow.Constants.CAMUNDA_VARIABLE_MUNICIPALITY_ID;
import static se.sundsvall.workflow.Constants.CAMUNDA_VARIABLE_NAMESPACE;
import static se.sundsvall.workflow.Constants.CAMUNDA_VARIABLE_REQUEST_ID;

public final class CamundaMapper {

	private CamundaMapper() {}

	public static StartProcessInstanceDto toStartProcessInstanceDto(final String municipalityId, final String namespace, final String businessKey) {
		return new StartProcessInstanceDto()
			.businessKey(businessKey)
			.variables(Map.of(
				CAMUNDA_VARIABLE_MUNICIPALITY_ID, toVariableValueDto(ValueType.STRING, municipalityId),
				CAMUNDA_VARIABLE_NAMESPACE, toVariableValueDto(ValueType.STRING, namespace),
				CAMUNDA_VARIABLE_REQUEST_ID, toVariableValueDto(ValueType.STRING, RequestId.get())));
	}

	public static VariableValueDto toVariableValueDto(final ValueType valueType, final Object value) {
		return new VariableValueDto()
			.type(valueType.getName())
			.value(value);
	}

	public static PatchVariablesDto toPatchVariablesDto(final Map<String, VariableValueDto> variablesToUpdate) {
		return new PatchVariablesDto()
			.modifications(variablesToUpdate);
	}
}
