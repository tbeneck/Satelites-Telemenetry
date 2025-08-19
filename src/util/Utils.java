package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;

public class Utils {
	
	// Get time interval between input alert messages
		public double getViolationTimeInterval(List<String> timeStampsList) {
			double max = 0.00;
			double min = 0.00;
			
			ArrayList<Double> numericValue = new ArrayList<Double>();
			
			if(timeStampsList.size() != 0) {
			for (int i = 0; i < timeStampsList.size(); i++) {
				String timestamp = timeStampsList.get(i);
				String newTimestamp = timestamp.substring(8, 14).replace(':', '.');
				newTimestamp = newTimestamp + timestamp.substring(15, 21).replace(".", "");
				numericValue.add(Double.parseDouble(newTimestamp));
			}
			max = Collections.max(numericValue);
			min = Collections.min(numericValue);
			}
			
			double timeIntervalOfViolation = (max - min);
			
			return timeIntervalOfViolation;
		}

		// Format timestamp
		public String customTimeStampFormat(String timestamp) {
			StringBuilder stringBuilder = new StringBuilder(timestamp);

			stringBuilder.insert(4, '-');
			stringBuilder.insert(7, '-');
			stringBuilder.append("Z");

			String newFormat = stringBuilder.toString();
			newFormat = newFormat.replaceAll("\\s", "");

			return newFormat;
		}

		// Sort JSON in order satelliteId, severity, component, timestamp
		public String sortJSONProperties(JSONArray myJsonArray) {
			String satelliteId = null;
			String severity = null;
			String component = null;
			String timestamp = null;
			String format = "";
			format = format.trim();
			String finalFormat = "[\n";
			for (int i = 0; i < myJsonArray.length(); i++) {
				satelliteId = myJsonArray.getJSONObject(i).get("satelliteId").toString();
				severity = myJsonArray.getJSONObject(i).get("severity").toString();
				component = myJsonArray.getJSONObject(i).get("component").toString();
				timestamp = myJsonArray.getJSONObject(i).get("timestamp").toString();

				format = "  {\r\n" + "    \"satelliteId\"" + ": " + "\"" + satelliteId + "\"" + ",\r\n" + "    \"severity\""
						+ ": " + "\"" + severity + "\"" + ",\r\n" + "    \"component\"" + ": " + "\"" + component + "\""
						+ ",\r\n" + "    \"timestamp\"" + ": " + "\"" + timestamp + "\"" + "\r\n" + "  },\n";

				finalFormat += format;
			}
			return finalFormat = finalFormat.substring(0, finalFormat.trim().length() - 1) + "\n]";
		}
}