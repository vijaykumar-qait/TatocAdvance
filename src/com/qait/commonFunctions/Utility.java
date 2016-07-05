package com.qait.commonFunctions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Utility {
	public static String getConfigValue(String value) {
		Map<String, String> config= new HashMap<>();
		try {
			BufferedReader fin= new BufferedReader(new FileReader(new File("requirement.config")));
			String line= "";
			while((line= fin.readLine()) != null) {
				config.put(line.split("=")[0], line.split("=")[1]);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return config.get(value);
	}
}
