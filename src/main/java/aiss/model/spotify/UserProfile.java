package aiss.model.spotify;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {
	private String country;
	@JsonProperty("display_name")
	private String displayName;
	private String id;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@JsonGetter("display_name")
	public String getDisplayName() {
		return displayName;
	}

	@JsonSetter("display_name")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}