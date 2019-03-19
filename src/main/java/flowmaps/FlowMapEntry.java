package flowmaps;

public class FlowMapEntry {

	/** Describes a flow of the source system of a conversion. */
	public FlowRef sourceFlow;

	/** Describes the corresponding flow of the target system. */
	public FlowRef targetFlow;

	/**
	 * An optional conversion factor which is applied to the amounts of the
	 * source flow to convert them into the corresponding amounts of the target
	 * flow (in the respective flow properties and units); defaults to 1.0
	 */
	public double factor;

}