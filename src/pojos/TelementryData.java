package pojos;

//Telementry Data POJO(Plain Old Java Object)
public class TelementryData {

	public TelementryData(String timestamp, int satelliteId, int redHighLimit, int yellowHighLimit,
			int yellowLowLimit, int redLowLimit, double rawValue, String component) {
		super();
		this.timestamp = timestamp;
		this.satelliteId = satelliteId;
		this.redHighLimit = redHighLimit;
		this.yellowHighLimit = yellowHighLimit;
		this.yellowLowLimit = yellowLowLimit;
		this.redLowLimit = redLowLimit;
		this.rawValue = rawValue;
		this.component = component;
	}
	
	private String timestamp;
	private String severity;
	private int satelliteId;
	private int redHighLimit;
	private int yellowHighLimit;
	private int yellowLowLimit;
	private int redLowLimit;
	private double rawValue;
	
	private String component;


	public int getSatelliteId() {
		return satelliteId;
	}

	public void setSatelliteId(int satelliteId) {
		this.satelliteId = satelliteId;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public int getRedHighLimit() {
		return redHighLimit;
	}

	public void setRedHighLimit(int redHighLimit) {
		this.redHighLimit = redHighLimit;
	}

	public int getYellowHighLimit() {
		return yellowHighLimit;
	}

	public void setYellowHighLimit(int yellowHighLimit) {
		this.yellowHighLimit = yellowHighLimit;
	}

	public int getYellowLowLimit() {
		return yellowLowLimit;
	}

	public void setYellowLowLimit(int yellowLowLimit) {
		this.yellowLowLimit = yellowLowLimit;
	}
	
	public double getRawValue() {
		return rawValue;
	}

	public void setRawValue(double rawValue) {
		this.rawValue = rawValue;
	}

	public int getRedLowLimit() {
		return redLowLimit;
	}

	public void setRedLowLimit(int redLowLimit) {
		this.redLowLimit = redLowLimit;
	}

}