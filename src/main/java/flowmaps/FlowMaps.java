package flowmaps;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.openlca.core.database.FlowDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Flow;
import org.openlca.core.model.descriptors.FlowDescriptor;
import org.openlca.jsonld.EntityStore;

import flowmaps.FlowMapEntry.SyncState;

public class FlowMaps {

	private final EntityStore jstore;
	private final IDatabase db;

	private FlowMaps(EntityStore jstore, IDatabase db) {
		this.jstore = jstore;
		this.db = db;
	}

	public static void sync(FlowMap map, EntityStore store, IDatabase db) {
		if (map == null || store == null || db == null)
			return;
		for (FlowMapEntry e : map.entries) {

		}

	}

	public static void apply(FlowMap map, EntityStore store, IDatabase db) {
	}

	private void sync(FlowMap map) {
		if (map == null)
			return;
		FlowDao dao = new FlowDao(db);
		Map<String, Flow> sourceFlows = dao.getAll()
				.stream().collect(Collectors
						.toMap(d -> d.refId, d -> d));
		HashSet<String> handled= new HashSet<>();

		for (FlowMapEntry e : map.entries) {

			// get the source flow
			if (e.sourceFlow == null || e.sourceFlow.flow == null) {
				e.syncState = SyncState.INVALID_SOURCE;
				continue;
			}
			String sourceID = e.sourceFlow.flow.refId;
			if (sourceID == null) {
				e.syncState = SyncState.INVALID_SOURCE;
				continue;
			}
			if (handled.contains(sourceID)) {
				e.syncState = SyncState.DUPLICATE;
				continue;
			}
			Flow sourceFlow = sourceFlows.get(sourceID);
			if (sourceFlow == null) {
				e.syncState = SyncState.UNFOUND_SOURCE;
				continue;
			}
			// TODO: validate a possible flow property and unit
			if (e.targetFlow == null || e.targetFlow.flow == null) {
				e.syncState = SyncState.INVALID_TARGET;
				continue;
			}


		}
	}
}
