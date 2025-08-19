package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pojos.TelemetryData;
import service.AlertMessageService;
import util.Utils;

public class UnitTests {

	AlertMessageService ams = new AlertMessageService();

	@Test
	public void testfinalJSONString() {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		Utils utils = new Utils();

		jsonObject.put("satelliteId", "11111");
		jsonObject.put("severity", "Red Low Test");
		jsonObject.put("component", "test");
		jsonObject.put("timestamp", "test");

		jsonArray.put(jsonObject);

		String finalJSONString = utils.sortJSONProperties(jsonArray);

		System.out.println(finalJSONString);

		boolean test = false;
		if (finalJSONString.contains("satelliteId")) {
			test = true;
		}

		System.out.println(finalJSONString);

		assertTrue(test);
		assertTrue(!finalJSONString.isEmpty());

	}

	@Test
	public void testTimeIntervals() {

		String stampa = "20180101 23:03:05.009";
		String stampb = "20180101 23:04:11.531";
		String stampc = "20180101 23:05:05.021";
		String stampd = "20180101 23:05:07.421";

		ArrayList<String> timestamps = new ArrayList<String>();

		timestamps.add(stampa);
		timestamps.add(stampb);
		timestamps.add(stampc);
		timestamps.add(stampd);

		Utils utils = new Utils();

		double timeInterval = utils.getViolationTimeInterval(timestamps);

		assertEquals(timeInterval, 0.02024120000000096);

		System.out.println(timeInterval);
	}

	public void testViolationMessages() {

		TelemetryData telemetryData = new TelemetryData("20180101 23:05:07.421", 1002, 120, 80, 80, 120, 44.5, "TSAT");

		TelemetryData telemetryData2 = new TelemetryData("20180101 23:06:07.421", 1003, 121, 81, 81, 121, 44.7, "BTT");

		ArrayList<TelemetryData> telemetryDataList = new ArrayList<TelemetryData>();

		telemetryDataList.add(telemetryData);
		telemetryDataList.add(telemetryData2);
		assertThrows(NullPointerException.class, () -> ams.generateViolationsMessages(telemetryDataList));

	}

}
