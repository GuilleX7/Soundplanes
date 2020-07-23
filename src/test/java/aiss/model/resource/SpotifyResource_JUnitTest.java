package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.spotify.Playlist;
import aiss.model.spotify.PlaylistTrack;
import aiss.model.spotify.Track;
import aiss.model.spotify.UserProfile;

public class SpotifyResource_JUnitTest {
	/* CHANGE THESE VARIABLES IN ORDER TO EXECUTE THE TEST
	 * TO OBTAIN A VALID TOKEN, GO TO
	 * https://developer.spotify.com/console/get-current-user/
	 * LOG IN, CLICK "GET TOKEN" AND CHECK "playlist-modify-private",
	 * "playlist-modify-public" and "playlist-read-private"
	 * THEN CHANGE token HERE
	 */
	/* START OF MANUAL DATA */
	final static String token = "BQAXV6p0KHhQw_y2NsBg5TjUkorO5E54KUk2nUt-yNEwmvCJLqMcYtpwlfDk2PCh8L4ol1jy-j2fv619gk2gwpF8YrruOXVMgsZAffFozqUAsA1nNcKoxUgacla50y09UfL8_3tYdhS-lhVQVYYwfMD999XtuEWxHRyVT_DgqH_O3Cee-ZsCqoqCsedf9lRp2EIkgJyz6RL3gFdHVMxXhp8";
	/* END OF MANUAL DATA*/
	
	static String userId = null;
	static Playlist testPlaylist = null;
	
	@BeforeClass
	public static void init() {
		userId = SpotifyResource.fromToken(token).getUserProfile().getId();
		testPlaylist = SpotifyResource.fromToken(token).createEmptyPlaylist(userId, "test", false, "test");
	}
	
	@Ignore
	@Test
	public void getPlaylistsTest() throws ResourceException, IOException {
		List<Playlist> playlistsResults = SpotifyResource.fromToken(token).getPlaylists();

		assertNotNull("The search returned null", playlistsResults);
		assertFalse("There are no playlists ", playlistsResults.isEmpty());
	}

	@Ignore
	@Test
	public void getPlaylistTracksTest() throws ResourceException, IOException {
		final String playlistId = "37i9dQZEVXbNFJfN1Vw8d9";
		List<PlaylistTrack> playlistsTracksResults = SpotifyResource.fromToken(token).getPlaylistTracks(playlistId);

		assertNotNull("The search returned null", playlistsTracksResults);
		assertFalse("There are no playlist tracks ", playlistsTracksResults.isEmpty());
	}

	@Ignore
	@Test
	public void getPlaylistTest() throws ResourceException, IOException {
		final String playlistId = "37i9dQZEVXbNFJfN1Vw8d9";
		Playlist playlistResults = SpotifyResource.fromToken(token).getPlaylist(playlistId);

		assertNotNull("The search returned null", playlistResults);
	}

	@Ignore
	@Test
	public void createEmptyPlaylistTest() throws ResourceException, IOException {
		final String userId = "guillex7";
		final String name = "Test";
		final Boolean isPublic = false;
		final String description = "description";

		Playlist emptyPlaylistResults = SpotifyResource.fromToken(token).createEmptyPlaylist(userId, name, isPublic,
				description);
		assertNotNull("Returned null", emptyPlaylistResults);
	}

	@Ignore
	@Test
	public void putTracksInPlaylistTest() throws ResourceException, IOException {
		final List<Track> tracks = new ArrayList<Track>();
		tracks.add(Track.of(null, null, null, "spotify:track:2DEZmgHKAvm41k4J3R2E9Y"));
		Boolean putTracksInPlaylistResults = SpotifyResource.fromToken(token).putTracksInPlaylist(testPlaylist.getId(), tracks);

		assertNotNull("Returned null", putTracksInPlaylistResults);
		assertTrue("Returned false", putTracksInPlaylistResults);
	}

	@Ignore
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		UserProfile userProfileResults = SpotifyResource.fromToken(token).getUserProfile();

		assertNotNull("Returned null", userProfileResults);
	}

}
