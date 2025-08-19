package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pojos.TelemetryData;
import util.Utils;

public class AlertMessageService {

	ArrayList<String> timestamps = new ArrayList<String>();

	Utils utils = new Utils();

	int[] uniqueSatelliteIds;

	int satelliteIdOne;

	int satelliteIdTwo;

	// Get all input message lines
	public List<String> getInputMessage() {

		List<String> inputLinesList = new ArrayList<String>();

		// Relative path so it is compatible on repository
		File currentDir = new File("");
		String currentDirString = currentDir.getAbsolutePath();
		//String path = currentDirString + "/telemetry input/telemetry data.txt";
		
			//Custom Inputs
			//String path = currentDirString + "/telemetry custom input/custom telemetry data.txt";
	//		String path = currentDirString + "/telemetry custom input/custom telemetry data2.txt";
	//		String path = currentDirString + "/telemetry custom input/custom telemetry data3.txt";
	//	String path = currentDirString + "/telemetry custom input/custom telemetry data4.txt";
			String path = currentDirString + "/telemetry custom input/custom telemetry data5.txt";

		path = path.replaceAll("\\\\", "/");

		// get all lines from input file
		try {
			Stream<String> stream;
			stream = Files.lines(Paths.get(path));
			inputLinesList = Files.readAllLines(Paths.get(path));
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputLinesList;
	}

	// Create List of Telemetry Data Objects
	public List<TelemetryData> getTelemetryData(List<String> inputSignals) {

		int[] satelliteIds = new int[inputSignals.size()];
		List<TelemetryData> telemetryDataList = new ArrayList<TelemetryData>();
		for (int i = 0; i < inputSignals.size(); i++) {

			// Split line on each | for each TelemetryData Field
			String[] lines = inputSignals.get(i).split("\\|");

			String timestamp = lines[0];
			int sateliteId = Integer.parseInt(lines[1]);
			satelliteIds[i] = sateliteId;
			int redHighLimit = Integer.parseInt(lines[2]);
			int yellowHighLimit = Integer.parseInt(lines[3]);
			int yellowLowLimit = Integer.parseInt(lines[4]);
			int redLowLimit = Integer.parseInt(lines[5]);
			double rawValue = Double.parseDouble(lines[6]);
			String component = lines[7];

			// Constructor to create telemetry data object using the constructor
			// from the input
			TelemetryData telemetryData = new TelemetryData(timestamp, sateliteId, redHighLimit, yellowHighLimit,
					yellowLowLimit, redLowLimit, rawValue, component);

			telemetryDataList.add(telemetryData);
		}
		// Validate input data is from 2 unique Satellites
		int[] uniqueSatelliteIds = numberOfUniqueIntegers(satelliteIds);

		this.uniqueSatelliteIds = uniqueSatelliteIds;

		if (uniqueSatelliteIds.length != 2) {
			System.err.println("Invalid input data must be from two unique satellites");
			System.exit(0);
		}
		return telemetryDataList;
	}

	// Generate out bound violation messages for each satellite
	public List<List<TelemetryData>> generateViolationsMessages(List<TelemetryData> telemetryData) {

		// Create two separate lists for each satellite of type telemetry data
		List<TelemetryData> satelliteOneList = new ArrayList<TelemetryData>();
		List<TelemetryData> satelliteTwoList = new ArrayList<TelemetryData>();

		// Satellite one violations
		List<TelemetryData> satelliteOneLowViolations = new ArrayList<TelemetryData>();
		List<TelemetryData> satelliteOneHighViolations = new ArrayList<TelemetryData>();

		// Satellite two violations
		List<TelemetryData> satelliteTwoLowViolations = new ArrayList<TelemetryData>();
		List<TelemetryData> satelliteTwoHighViolations = new ArrayList<TelemetryData>();

		for (int i = 0; i < telemetryData.size(); i++) {
			int satelliteId = telemetryData.get(i).getSatelliteId();
			if (satelliteId == this.uniqueSatelliteIds[0]) {
				satelliteOneList.add(telemetryData.get(i));
			} else {
				satelliteTwoList.add(telemetryData.get(i));
			}
		}

		// check for violations in satellite one
		for (int i = 0; i < satelliteOneList.size(); i++) {

			if (satelliteOneList.get(i).getRawValue() < satelliteOneList.get(i).getRedLowLimit()) {
				satelliteOneLowViolations.add(satelliteOneList.get(i));
			}
			if (satelliteOneList.get(i).getRawValue() > satelliteOneList.get(i).getRedHighLimit()) {
				satelliteOneHighViolations.add(satelliteOneList.get(i));
			}
		}

		// check for violations satellite two
		for (int i = 0; i < satelliteTwoList.size(); i++) {
			if (satelliteTwoList.get(i).getRawValue() < satelliteTwoList.get(i).getRedLowLimit()) {
				satelliteTwoLowViolations.add(satelliteTwoList.get(i));
			}
			if (satelliteTwoList.get(i).getRawValue() > satelliteTwoList.get(i).getRedHighLimit()) {
				satelliteTwoHighViolations.add(satelliteTwoList.get(i));
			}
		}
		// Return violations List of Lists for both satellites
		List<List<TelemetryData>> violationsList = new ArrayList<List<TelemetryData>>();
		violationsList.add(satelliteOneLowViolations);
		violationsList.add(satelliteOneHighViolations);
		violationsList.add(satelliteTwoLowViolations);
		violationsList.add(satelliteTwoHighViolations);

		return violationsList;
	}

	// Create alert messages from Violations Lists
	public void createAlertMessages(List<List<TelemetryData>> violationsLists) {
		JSONArray jsonArray = new JSONArray();

		int satelliteOneLowViolationsCount = violationsLists.get(0).size(); // Satellite One Low Violations
		int satelliteOneHighViolationsCount = violationsLists.get(1).size(); // Satellite One High Violations
		int satelliteTwoLowViolationsCount = violationsLists.get(2).size(); // Satellite Two Low Violations
		int satelliteTwoHighViolationsCount = violationsLists.get(3).size(); // Satellite Two High Violations

		for (int i = 0; i < violationsLists.get(0).size(); i++) {
			this.timestamps.add(violationsLists.get(0).get(i).getTimestamp());
		}

		double timeIntervalOfViolationOneLow = utils.getViolationTimeInterval(timestamps);

		JSONObject jsonObjectSatelliteOneLow = new JSONObject();
		JSONObject jsonObjectSatelliteOneHigh = new JSONObject();
		JSONObject jsonObjectSatelliteTwoLow = new JSONObject();
		JSONObject jsonObjectSatelliteTwoHigh = new JSONObject();

		if (satelliteOneLowViolationsCount >= 3 && timeIntervalOfViolationOneLow < 5) {
			for (int i = 0; i < 1; i++) {

				jsonObjectSatelliteOneLow.put("satelliteId", violationsLists.get(0).get(i).getSatelliteId());
				jsonObjectSatelliteOneLow.put("severity", "RED LOW");
				jsonObjectSatelliteOneLow.put("component", violationsLists.get(0).get(i).getComponent());
				jsonObjectSatelliteOneLow.put("timestamp", utils.customTimeStampFormat(violationsLists.get(0).get(i).getTimestamp()));

				jsonArray.put(jsonObjectSatelliteOneLow);

			}
		} else {
			jsonObjectSatelliteOneLow = null;
		}

		for (int i = 0; i < violationsLists.get(1).size(); i++) {
			timestamps.add(violationsLists.get(1).get(i).getTimestamp());
		}

		double timeIntervalOfViolationOneHigh = utils.getViolationTimeInterval(timestamps);

		if (satelliteOneHighViolationsCount >= 3 && timeIntervalOfViolationOneHigh < 5) {
			for (int i = 0; i < 1; i++) {

				jsonObjectSatelliteOneHigh.put("satelliteId", violationsLists.get(1).get(i).getSatelliteId());
				jsonObjectSatelliteOneHigh.put("severity", "RED HIGH");
				jsonObjectSatelliteOneHigh.put("component", violationsLists.get(1).get(i).getComponent());
				jsonObjectSatelliteOneHigh.put("timestamp", utils.customTimeStampFormat(violationsLists.get(1).get(i).getTimestamp()));

				jsonArray.put(jsonObjectSatelliteOneHigh);
			}
		} else {
			jsonObjectSatelliteOneHigh = null;
		}

		for (int i = 0; i < violationsLists.get(3).size(); i++) {
			timestamps.add(violationsLists.get(3).get(i).getTimestamp());
		}
		double timeIntervalOfSatTwoHighViolations = utils.getViolationTimeInterval(timestamps);

		if (satelliteTwoHighViolationsCount >= 3 && timeIntervalOfSatTwoHighViolations < 5) {

			for (int i = 0; i < 1; i++) {

				jsonObjectSatelliteTwoHigh.put("satelliteId", violationsLists.get(3).get(i).getSatelliteId());
				jsonObjectSatelliteTwoHigh.put("severity", "RED HIGH");
				jsonObjectSatelliteTwoHigh.put("component", violationsLists.get(3).get(i).getComponent());
				jsonObjectSatelliteTwoHigh.put("timestamp", utils.customTimeStampFormat(violationsLists.get(3).get(i).getTimestamp()));

				jsonArray.put(jsonObjectSatelliteTwoHigh);

			}
		} else {
			jsonObjectSatelliteTwoHigh = null;
		}

		for (int i = 0; i < violationsLists.get(2).size(); i++) {
			timestamps.add(violationsLists.get(2).get(i).getTimestamp());
		}
		double timeIntervalOfSatTwoLowViolations = utils.getViolationTimeInterval(timestamps);

		if (satelliteTwoLowViolationsCount >= 3 && timeIntervalOfSatTwoLowViolations < 5) {
			for (int i = 0; i < 1; i++) {

				jsonObjectSatelliteTwoLow.put("satelliteId", violationsLists.get(2).get(i).getSatelliteId());
				jsonObjectSatelliteTwoLow.put("severity", "RED LOW");
				jsonObjectSatelliteTwoLow.put("component", violationsLists.get(2).get(i).getComponent());
				jsonObjectSatelliteTwoLow.put("timestamp", utils.customTimeStampFormat(violationsLists.get(2).get(i).getTimestamp()));

				jsonArray.put(jsonObjectSatelliteTwoLow);
			}
		} else {
			jsonObjectSatelliteTwoLow = null;
		}

		// Sort JSON properties
		String finalJSON = utils.sortJSONProperties(jsonArray);
		// Prints the final JSON to the console as a String
		System.out.println(finalJSON);

		// Find relative path. Has to work on repository servers
		File currentDir = new File("");
		String currentDirString = currentDir.getAbsolutePath();
		String outputPath = currentDirString + "/alert messages/alert messages.json";
		outputPath = outputPath.replaceAll("\\\\", "/");

		// Write JSON file to the output folder
		// Refresh the project to view the output file called "Alert Messages.json"
		try {
			FileWriter jSONfile = new FileWriter(outputPath);
			jSONfile.write(finalJSON);
			jSONfile.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int[] numberOfUniqueIntegers(int[] numberOfSatellites) {
		IntStream satelliteIdsIntStream = Arrays.stream(numberOfSatellites).distinct();
		int[] satelliteIds = satelliteIdsIntStream.toArray();
		return satelliteIds;
	}
}