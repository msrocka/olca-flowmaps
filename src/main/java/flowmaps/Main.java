package flowmaps;

import java.io.File;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.jsonld.ZipStore;

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
		}

		db.close();
		store.close();
		System.out.println("all done");

	}
}
