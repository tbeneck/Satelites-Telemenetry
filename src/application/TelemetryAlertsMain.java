package application;

import java.util.List;

import pojos.TelementryData;
import service.AlertMessageService;

public class TelemetryAlertsMain {
	
	//Main method execution
	public static void main(String[] args) {

		AlertMessageService alertService = new AlertMessageService();

		List<String> alerts = alertService.getInputMessage();

		List<TelementryData> telementryData = alertService.getTelementryData(alerts);

		List<List<TelementryData>> violationsLists = alertService.generateViolationsMessages(telementryData);

		alertService.createAlertMessages(violationsLists);
	}
}