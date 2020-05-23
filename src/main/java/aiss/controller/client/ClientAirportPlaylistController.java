package aiss.controller.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiss.model.genius.Song;
import aiss.model.resource.AirportResource;
import aiss.model.resource.GeniusResource;
import aiss.model.resource.SpotifyResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;
import aiss.model.spotify.Playlist;
import aiss.model.spotify.PlaylistTrack;
import aiss.model.spotify.Track;

/**
 * Servlet implementation class ClientAirportTrackController
 */
public class ClientAirportPlaylistController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClientAirportPlaylistController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ClientResponse cr = ClientResponse.create();

		String uuid = (String) request.getSession().getAttribute("UUID");
		if (uuid == null) {
			cr.setStatus(ClientResponseStatus.UNAUTHORIZED);
			cr.writeTo(response);
			return;
		}

		User user = UserResource.getUser(uuid);
		if (user == null) {
			cr.setStatus(ClientResponseStatus.INTERNAL_ERROR);
			cr.writeTo(response);
			return;
		}

		if (!user.isLanded()) {
			cr.setStatus(ClientResponseStatus.NO_CONTENT);
			cr.writeTo(response);
			return;
		}

		Airport airport = AirportResource.getAirport(user.getLandedOn());
		if (airport == null) {
			cr.setStatus(ClientResponseStatus.NOT_FOUND);
			cr.setData(user.getLandedOn());
			cr.writeTo(response);
			return;
		}

		if (!airport.isPlaylistLoaded()) {
			cr.setStatus(ClientResponseStatus.NO_CONTENT);
			cr.writeTo(response);
			return;
		}
		Playlist playlistInfo = airport.getPlaylistInfo();
		
		AirportPlaylist playlist = AirportResource.getAirportPlaylist(airport.getUuid());
		if (playlist == null || playlist.getTracks() == null || playlist.getTracks().size() < 1) {
			cr.setStatus(ClientResponseStatus.NO_CONTENT);
			cr.writeTo(response);
			return;
		}
		
		Track track = playlist.getRandomTrack();
		String lyrics = null;
		
		List<Song> songs = GeniusResource.searchSong(track.getFullName());
	
		String title = null;
		for (int i = 0; i < songs.size(); i++) {
			title = songs.get(i).getFullTitle().toLowerCase();
			if (title.contains("genius") || title.contains("spotify")) continue;
			lyrics = GeniusResource.getLyricsFromSong(songs.get(i));
			if (lyrics != null) {
				break;
			}
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("track", track);
		data.put("lyrics", lyrics);
		data.put("playlistInfo", playlistInfo);
		
		cr.setData(data);
		cr.setStatus(ClientResponseStatus.OK);
		cr.writeTo(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ClientResponse cr = ClientResponse.create();

		String uuid = (String) request.getSession().getAttribute("UUID");
		if (uuid == null) {
			cr.setStatus(ClientResponseStatus.UNAUTHORIZED);
			cr.writeTo(response);
			return;
		}

		User user = UserResource.getUser(uuid);
		if (user == null) {
			cr.setStatus(ClientResponseStatus.INTERNAL_ERROR);
			cr.writeTo(response);
			return;
		}
		
		Airport airport = AirportResource.getAirport(user.getUuid());
		if (airport == null) {
			cr.setStatus(ClientResponseStatus.NOT_FOUND);
			cr.setData(user.getUuid());
			cr.writeTo(response);
			return;
		}
		
		String playlistId = request.getParameter("playlistId");
		if (playlistId == null) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
			return;
		}
		
		if (user.getSpotifyId() == null) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
			return;
		}

		String spotifyToken = (String) request.getSession().getAttribute("Spotify-token");
		if (spotifyToken == null) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
			return;
		}

		SpotifyResource sr = SpotifyResource.fromToken(spotifyToken);
		Playlist playlist = sr.getPlaylist(playlistId);
		if (playlist == null) {
			cr.setStatus(ClientResponseStatus.SEE_OTHER);
			cr.setData("/auth/spotify");
			cr.writeTo(response);
			return;
		}
		
		List<PlaylistTrack> playlistTracks = sr.getPlaylistTracks(playlist);
		if (playlistTracks == null) {
			cr.setStatus(ClientResponseStatus.SEE_OTHER);
			cr.setData("/auth/spotify");
			cr.writeTo(response);
			return;
		}
		
		List<Track> tracks = new ArrayList<Track>();
		for (PlaylistTrack playlistTrack : playlistTracks) {
			tracks.add(playlistTrack.getTrack());
		}
		
		AirportPlaylist airportPlaylist = AirportResource.getAirportPlaylist(airport.getUuid());
		airportPlaylist.setTracks(tracks);
		AirportResource.registerAirportPlaylist(airportPlaylist);
		airport.setPlaylistInfo(playlist);
		AirportResource.registerAirport(airport);
		
		cr.setStatus(ClientResponseStatus.OK);
		cr.setData(playlist);
		cr.writeTo(response);
	}

}
