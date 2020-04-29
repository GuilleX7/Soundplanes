package aiss.model.user;

public class Location {
	private Double latitude;
	private Double longitude;
	
	public static Location ofCoordinates(Double latitude, Double longitude) {
		return new Location(latitude, longitude);
	}
	
	public static Location fromFormat(String format) {
		String[] coordinates = format.split(",");
		return new Location(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
	}
	
	private Location(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
}
