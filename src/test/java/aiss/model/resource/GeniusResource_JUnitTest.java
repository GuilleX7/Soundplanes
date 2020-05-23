package aiss.model.resource;

import static org.junit.Assert.assertFalse;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.genius.Song;

public class GeniusResource_JUnitTest {
	
	@Test
	public void searchSongTest() throws ResourceException, IOException {
		final String title = "Bohemian Rhapsody";
		List<Song> songResults = GeniusResource.searchSong(title);

		assertNotNull("The search returned null", songResults);
		assertFalse("There is no song called: " + title, songResults.isEmpty());
	}

	@Test
	public void getLyricsFromSongTest() throws IOException {
		final String fullTitle = "Bohemian Rhapsody Queen";
		final Integer id = 12;
		final String songArtThumnailUrl = "song_art_image_thumbnail_url";
		final String title = "Bohemian Rhapsody";
		final String titleWithFeatured = "title_with_featured";
		final String url = "https://genius.com/Queen-bohemian-rhapsody-lyrics";
		final Song s = Song.of(fullTitle, id, songArtThumnailUrl, title, titleWithFeatured, url);

		String lyricsResults = GeniusResource.getLyricsFromSong(s);

		assertNotNull("The search returned null", lyricsResults);
	}
}
