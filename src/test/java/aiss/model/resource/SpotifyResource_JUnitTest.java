package aiss.model.resource;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.spotify.Image;
import aiss.model.spotify.Playlist;
import aiss.model.spotify.PlaylistTrack;
import aiss.model.spotify.Track;
import aiss.model.spotify.UserProfile;

public class SpotifyResource_JUnitTest {
	
	final String token="";
	
	@Test
	public void getPlaylistsTest() throws ResourceException, IOException {
		List<Playlist> playlistsResults = SpotifyResource.fromToken(token).getPlaylists();

		assertNotNull("The search returned null", playlistsResults);
		assertFalse("There are no playlists ", playlistsResults.isEmpty());
	}
	
	@Test
	public void getPlaylistTracksTest() throws ResourceException, IOException {
		final String description="description";
		final String id="432";
		final List<Image> images= new ArrayList<>();
		final String name= "Disney";
		final Playlist playlist= Playlist.of(description, id, images, name);
		List<PlaylistTrack> playlistsTracksResults = SpotifyResource.fromToken(token).getPlaylistTracks(playlist);

		assertNotNull("The search returned null", playlistsTracksResults);
		assertFalse("There are no playlist tracks ", playlistsTracksResults.isEmpty());
	}
	
	@Test
	public void getPlaylistTest() throws ResourceException, IOException {
		final String playlistId= "123";
		Playlist playlistResults = SpotifyResource.fromToken(token).getPlaylist(playlistId);

		assertNotNull("The search returned null", playlistResults);
	}
	
	@Test
	public void createEmptyPlaylistTest() throws ResourceException, IOException {
		final String userId="userId";
		final String name="name";
		final Boolean isPublic=true;
		final String description="description";
		Playlist emptyPlaylistResults = SpotifyResource.fromToken(token)
				.createEmptyPlaylist(userId, name, isPublic, description);
		assertNotNull("Returned null", emptyPlaylistResults);
	}
	
	@Test
	public void putTracksInPlaylistTest() throws ResourceException, IOException {
		final String description="description";
		final String id="432";
		final List<Image> images= new ArrayList<>();
		final String name= "Disney";
		final Playlist playlist= Playlist.of(description, id, images, name);
		final List<Track> tracks=new ArrayList<>();
		Boolean putTracksInPlaylistResults = SpotifyResource.fromToken(token)
				.putTracksInPlaylist(playlist, tracks);
		assertNotNull("Returned null", putTracksInPlaylistResults);
		assertTrue("Returned false",putTracksInPlaylistResults);
	}
	
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		UserProfile userProfileResults= SpotifyResource.fromToken(token).getUserProfile();
		assertNotNull("Returned null", userProfileResults);
		
	}


}
