package aiss.model.geocoding;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
	@JsonProperty("lat")
	private Double latitude;
	@JsonProperty("lng")
	private Double longitude;
	
	public static Location ofCoordinates(Double latitude, Double longitude) {
		return new Location(latitude, longitude);
	}
	
	public static Location fromFormat(String format) {
		String[] coordinates = format.split(",");
		return new Location(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
	}
	
	private Location() {
		super();
		this.latitude = 0.0;
		this.longitude = 0.0;
	}
	
	private Location(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@JsonGetter("lat")
	public Double getLatitude() {
		return latitude;
	}
	
	@JsonSetter("lat")
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@JsonGetter("lng")
	public Double getLongitude() {
		return longitude;
	}
	
	@JsonSetter("lng")
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Location [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
}
