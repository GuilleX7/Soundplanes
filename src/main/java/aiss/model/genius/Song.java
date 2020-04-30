package aiss.model.genius;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {
	@JsonProperty("full_title")
	private String fullTitle;
	private Integer id;
	@JsonProperty("song_art_image_thumbnail_url")
	private String songArtThumnailUrl;
	private String title;
	@JsonProperty("title_with_featured")
	private String titleWithFeatured;
	private String url;
	
	@JsonGetter("full_title")
	public String getFullTitle() {
		return fullTitle;
	}
	
	@JsonSetter("full_title")
	public void setFullTitle(String fullTitle) {
		this.fullTitle = fullTitle;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@JsonGetter("song_art_image_thumbnail_url")
	public String getSongArtThumnailUrl() {
		return songArtThumnailUrl;
	}
	
	@JsonSetter("song_art_image_thumbnail_url")
	public void setSongArtThumnailUrl(String songArtThumnailUrl) {
		this.songArtThumnailUrl = songArtThumnailUrl;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@JsonGetter("title_with_featured")
	public String getTitleWithFeatured() {
		return titleWithFeatured;
	}
	
	@JsonSetter("title_with_featured")
	public void setTitleWithFeatured(String titleWithFeatured) {
		this.titleWithFeatured = titleWithFeatured;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}