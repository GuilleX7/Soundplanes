package aiss.model.genius;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hit {
	private String type;
	@JsonProperty("result")
	private Song song;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonGetter("result")
	public Song getSong() {
		return song;
	}
	
	@JsonSetter("result")
	public void setSong(Song song) {
		this.song = song;
	}
}
