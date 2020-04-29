package aiss.model.geocoding;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingRequest {
	private List<GeocodingResult> results;
	private String status;
	
	public List<GeocodingResult> getResults() {
		return results;
	}
	
	public void setResults(List<GeocodingResult> results) {
		this.results = results;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
