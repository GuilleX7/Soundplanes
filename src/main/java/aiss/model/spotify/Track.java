package aiss.model.spotify;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {
	private static Pattern nameSimplifier = Pattern.compile("([^-()]+)");
	
	private Album album;
	private List<Artist> artists;
	private String name;
	private String uri;
	
	public Album getAlbum() {
		return album;
	}
	
	public void setAlbum(Album album) {
		this.album = album;
	}
	
	public List<Artist> getArtists() {
		return artists;
	}
	
	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonIgnore
	public String getFullName() {
		Matcher m = Track.nameSimplifier.matcher(this.getName());
		String name = this.getName();
		if (m.find()) {
			name = m.group(1);
		}
		
		String artists = this.getArtists().get(0).getName();
		for (int i = 1; i < this.getArtists().size(); i++) {
			artists.concat(" & " + this.getArtists().get(i).getName());
		}
		return this.getArtists().get(0).getName() + " - " + name;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Track other = (Track) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
