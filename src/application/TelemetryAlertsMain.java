package application;

import java.util.List;

import pojos.TelemetryData;
import service.AlertMessageService;

public class TelemetryAlertsMain {
	
	//Main method execution
	public static void main(String[] args) {

		AlertMessageService alertService = new AlertMessageService();

		List<String> alerts = alertService.getInputMessage();

		List<TelemetryData> telemetryData = alertService.getTelemetryData(alerts);

		List<List<TelemetryData>> violationsLists = alertService.generateViolationsMessages(telemetryData);

		alertService.createAlertMessages(violationsLists);
	}
}