package aiss.model.spotify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {
	public static Playlist of(String description, String id, List<Image> images, String name) {
		return new Playlist(description, id, images, name);
	}

	private String description;
	private String id;
	private List<Image> images;
	private String name;
	
	

	private Playlist(String description, String id, List<Image> images, String name) {
		super();
		this.description = description;
		this.id = id;
		this.images = images;
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Image> getImages() {
		return images;
	}
	
	public void setImages(List<Image> images) {
		if (images.size() > 0) {
			this.images = images.subList(0, 1);
		} else {
			this.images = images;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Playlist other = (Playlist) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
