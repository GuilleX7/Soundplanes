package aiss.controller.client;

import java.io.IOException;
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
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;
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
			cr.setStatus(ClientResponseStatus.NOT_FOUND);
			cr.writeTo(response);
			return;
		}

		String airportUuid = user.getLandedOn();

		AirportPlaylist playlist = AirportResource.getAirportPlaylist(airportUuid);
		Track track = playlist.getRandomTrack();
		String lyrics = null;
		
		List<Song> songs = GeniusResource.searchSong(track.getFullName());
		if (songs.size() > 0) {
			lyrics = GeniusResource.getLyricsFromSong(songs.get(0));
		}
		
		Map<String, Object> lyricTrack = new HashMap<String, Object>();
		lyricTrack.put("track", track);
		lyricTrack.put("lyrics", lyrics);

		cr.setData(lyricTrack);
		
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

}
