package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;

import aiss.model.genius.Song;

public class GeniusResource_JUnitTest {
	
	@Test
	public void searchSongTest() throws UnsupportedEncodingException {
		String title="Bohemian Rhapsody";
		GeniusResource song= new GeniusResource();
		List<Song> songResults= song.searchSong(title);
		
		assertNotNull("The search returned null", songResults);
		assertFalse("There is no song called: "+title,  songResults.isEmpty());
	}
	
	@Test
	public void getLyricsFromSongTest() throws IOException {
		Song s= new Song();
		GeniusResource song= new GeniusResource();
		String lyricsResults=song.getLyricsFromSong(s);
		assertNotNull("The search returned null", lyricsResults);
	}
}
