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
	private String songArtThumbnailUrl;
	private String title;
	@JsonProperty("title_with_featured")
	private String titleWithFeatured;
	private String url;

	public static Song of(String fullTitle, Integer id, String songArtThumbnailUrl, String title,
			String titleWithFeatured, String url) {
		return new Song(fullTitle, id, songArtThumbnailUrl, title, titleWithFeatured, url);
	}

	private Song(String fullTitle, Integer id, String songArtThumbnailUrl, String title, String titleWithFeatured,
			String url) {
		this.fullTitle = fullTitle;
		this.id = id;
		this.songArtThumbnailUrl = songArtThumbnailUrl;
		this.title = title;
		this.titleWithFeatured = titleWithFeatured;
		this.url = url;
	}

	private Song() {
		this.fullTitle = null;
		this.id = null;
		this.songArtThumbnailUrl = null;
		this.title = null;
		this.titleWithFeatured = null;
		this.url = null;
	}
	
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
	public String getSongArtThumbnailUrl() {
		return songArtThumbnailUrl;
	}

	@JsonSetter("song_art_image_thumbnail_url")
	public void setSongArtThumbnailUrl(String songArtThumnailUrl) {
		this.songArtThumbnailUrl = songArtThumnailUrl;
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
