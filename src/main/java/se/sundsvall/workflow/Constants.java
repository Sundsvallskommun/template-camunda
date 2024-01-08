package se.sundsvall.workflow;

import org.camunda.bpm.engine.variable.type.ValueType;

import generated.se.sundsvall.camunda.VariableValueDto;

public class Constants {

	private Constants() {}

	public static final String PROCESS_KEY = "template-camunda-process"; // Must match ID of process defined in bpmn schema
	public static final String TENANTID_TEMPLATE = "TEMPLATE_NAMESPACE"; // Namespace where process is deployed, a.k.a tenant (must match setting in application.yaml)

	public static final String UPDATE_AVAILABLE = "updateAvailable";
	public static final VariableValueDto TRUE = new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(true);
	public static final VariableValueDto FALSE = new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(false);
}
