package se.sundsvall.workflow;

import generated.se.sundsvall.camunda.VariableValueDto;
import org.camunda.bpm.engine.variable.type.ValueType;

public class Constants {

	private Constants() {}

	public static final String PROCESS_KEY = "template-camunda-process"; // Must match ID of process defined in bpmn schema
	public static final String TENANTID_TEMPLATE = "TEMPLATE_NAMESPACE"; // Namespace where process is deployed, a.k.a tenant (must match setting in application.yaml)

	public static final String NAMESPACE_REGEXP = "[\\w|\\-]+";
	public static final String NAMESPACE_VALIDATION_MESSAGE = "can only contain A-Z, a-z, 0-9, -, and _";

	public static final String UPDATE_AVAILABLE = "updateAvailable";
	public static final VariableValueDto TRUE = new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(true);
	public static final VariableValueDto FALSE = new VariableValueDto().type(ValueType.BOOLEAN.getName()).value(false);

	public static final String CAMUNDA_VARIABLE_BUSINESS_KEY = "businessKey";
	public static final String CAMUNDA_VARIABLE_REQUEST_ID = "requestId";
	public static final String CAMUNDA_VARIABLE_MUNICIPALITY_ID = "municipalityId";
	public static final String CAMUNDA_VARIABLE_NAMESPACE = "namespace";
	public static final String CAMUNDA_VARIABLE_UPDATE_AVAILABLE = "updateAvailable";
}
