package flowmaps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;

public class Main {

	public static void main(String[] args) {
		String dbPath = "C:/Users/ms/openLCA-data-1.4/databases/zepa_elci";
		IDatabase db = new DerbyDatabase(new File(dbPath));

		for (File f : new File("./target").listFiles()) {
			if (!f.getName().endsWith(".json"))
				continue;
			try (FileInputStream fis = new FileInputStream(f);
					Reader r = new InputStreamReader(fis, "utf-8");
					BufferedReader buf = new BufferedReader(r)) {
				JsonObject obj = new Gson().fromJson(buf, JsonObject.class);
				FlowMap map = FlowMap.from(obj);
				System.out.println("Sync flow map " + map.name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
