package flowmaps;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.jsonld.ZipStore;

import flowmaps.FlowMapEntry.SyncState;

public class Main {

	public static void main(String[] args) throws Exception {
		String dbPath = "C:/Users/ms/openLCA-data-1.4/databases/zepa_elci";
		IDatabase db = new DerbyDatabase(new File(dbPath));

		String zipPath = "./target/FedElemFlowList_0.2_json-ld.zip";
		ZipStore store = ZipStore.open(new File(zipPath));
		List<String> files = store.getFiles("flow_mappings");

		for (String f : files) {
			System.out.println("Read mapping from " + f);
			byte[] data = store.get(f);
			String json = new String(data, "utf-8");
			JsonObject obj = new Gson().fromJson(json, JsonObject.class);
			FlowMap map = FlowMap.from(obj);
			System.out.println("Sync flow map " + map.name);
			FlowMaps.sync(map, store, db);
			printStats(map);
		}

		db.close();
		store.close();
		System.out.println("all done");

	}

	private static void printStats(FlowMap map) {
		EnumMap<SyncState, Integer> stats = new EnumMap<>(SyncState.class);
		map.entries.forEach(e -> {
			stats.compute(e.syncState, (key, val) -> val == null ? 1 : val + 1);
		});
		List<SyncState> states = stats.keySet().stream()
				.sorted((s1, s2) -> stats.get(s2) - stats.get(s1))
				.collect(Collectors.toList());
		System.out.println("data = [");
		for (SyncState state : states) {
			System.out.println("  (\"" + state + "\", "
				+ stats.get(state) + "),");
		}
		System.out.println("]");
	}

}
