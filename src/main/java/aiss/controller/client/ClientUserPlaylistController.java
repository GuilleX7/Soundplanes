package aiss.controller.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiss.model.resource.SpotifyResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;
import aiss.model.spotify.Paging;
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

		Set<Playlist> playlists = new HashSet<Playlist>();
		SpotifyResource sr = SpotifyResource.fromToken(spotifyToken);
		Paging<Playlist> page = sr.getPlaylists(0, 20);
		
		if (page == null) {
			cr.setStatus(ClientResponseStatus.SEE_OTHER);
			cr.setData("/auth/spotify");
			cr.writeTo(response);
			return;
		}
			
		playlists.addAll(page.getItems());
		for (int i = 20; i < page.getTotal(); i += 20) {
			page = sr.getPlaylists(i, 20);
			playlists.addAll(page.getItems());
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

}
