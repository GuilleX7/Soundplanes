package aiss.model.spotify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {
	private List<Image> images;
	private String name;
	
	public List<Image> getImages() {
		return images;
	}
	
	public void setImages(List<Image> images) {
		this.images = images.subList(0, 1);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
