package flowmaps;

import org.openlca.core.model.descriptors.BaseDescriptor;
import org.openlca.core.model.descriptors.FlowDescriptor;

public class FlowRef {

	/**
	 * The reference to the flow data set.
	 */
	public FlowDescriptor flow;

	/**
	 * An optional reference to a property (= quantity) of the flow. When this
	 * is missing, the reference flow property of the flow is taken by default.
	 */
	public BaseDescriptor flowProperty;

	/**
	 * Also, the unit reference is optional; the reference unit of the unit
	 * group of the flow property is taken by default.
	 */
	public BaseDescriptor unit;
}
