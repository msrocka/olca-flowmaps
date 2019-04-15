package examples;

import java.io.File;

import flowmaps.IMapProvider;

public class GetTypeExample {

	public static void main(String[] args) {
		String dir = "C:/Users/Besitzer/Desktop/";
		System.out.println(IMapProvider.Type.of(
			new File(dir + "example_json_ld.zip")));
		System.out.println(IMapProvider.Type.of(
			new File(dir + "example_ilcd.zip")));
	}
}
