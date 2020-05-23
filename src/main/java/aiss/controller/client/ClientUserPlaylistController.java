package aiss.controller.client;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiss.model.resource.AirportResource;
import aiss.model.resource.SpotifyResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;
import aiss.model.spotify.Playlist;

/**
 * Servlet implementation class ClientUserPlaylistsController
 */
public class ClientUserPlaylistController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClientUserPlaylistController() {
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
		List<Playlist> playlists = sr.getPlaylists();
		
		if (playlists == null) {
			cr.setStatus(ClientResponseStatus.SEE_OTHER);
			cr.setData("/auth/spotify");
			cr.writeTo(response);
			return;
		}
		
		cr.setData(playlists);
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
		
		if (!user.isLanded()) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
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
		
		AirportPlaylist airportPlaylist = AirportResource.getAirportPlaylist(airport.getUuid());
		if (airportPlaylist == null || airportPlaylist.getTracks() == null || airportPlaylist.getTracks().size() < 1) {
			cr.setStatus(ClientResponseStatus.NO_CONTENT);
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
		
		Playlist userPlaylist = sr.createEmptyPlaylist(user.getSpotifyId(), playlistInfo.getName(), false, "Imported playlist from Soundplanes");
		if (userPlaylist == null) {
			cr.setStatus(ClientResponseStatus.SEE_OTHER);
			cr.setData("/auth/spotify");
			cr.writeTo(response);
			return;
		}
		
		Boolean success = sr.putTracksInPlaylist(userPlaylist, airportPlaylist.getTracks());
		if (!success) {
			cr.setStatus(ClientResponseStatus.SEE_OTHER);
			cr.setData("/auth/spotify");
			cr.writeTo(response);
			return;
		}
		
		cr.setStatus(ClientResponseStatus.CREATED);
		cr.writeTo(response);
	}
}
