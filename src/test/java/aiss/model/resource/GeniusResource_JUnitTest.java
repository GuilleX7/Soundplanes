package aiss.model.resource;

import static org.junit.Assert.assertFalse;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.genius.Song;

public class GeniusResource_JUnitTest {
	
	@Test
	public void searchSongTest() throws ResourceException, IOException {
		String title="Bohemian Rhapsody";
		List<Song> songResults= GeniusResource.searchSong(title);
		
		assertNotNull("The search returned null", songResults);
		assertFalse("There is no song called: "+title,  songResults.isEmpty());
	}
	
	
	
	
	@Test
	public void getLyricsFromSongTest() throws IOException {
		String fullTitle="Bohemian Rhapsody Queen";
		Integer id=12;
		String songArtThumnailUrl="song_art_image_thumbnail_url";
		String title="Bohemian Rhapsody";
		String titleWithFeatured="title_with_featured";
		String url="https://www.youtube.com/watch?v=fJ9rUzIMcZQ";
		
		Song s= Song.of(fullTitle,id,songArtThumnailUrl,title,titleWithFeatured,url);
		String lyricsResults=GeniusResource.getLyricsFromSong(s);
		
		assertNull("The search returned no null", lyricsResults);
	}
	
	
	
}
