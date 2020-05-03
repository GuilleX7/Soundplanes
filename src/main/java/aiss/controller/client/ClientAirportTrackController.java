package aiss.controller.client;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import aiss.model.genius.Song;
import aiss.model.resource.GeniusResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;

/**
 * Servlet implementation class ClientAirportTrackController
 */
public class ClientAirportTrackController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientAirportTrackController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ClientResponse cr = ClientResponse.create();

		String uuid = (String) request.getSession().getAttribute("UUID");
		if (uuid != null) {
			System.out.println("Client connected as " + uuid);
			User user = UserResource.getUser(uuid);
			System.out.println("User data: " + user);
			if (user != null) {
				String title = request.getParameter("title");
				if (title != null && !title.isEmpty()) {
					List<Song> songs = GeniusResource.searchSong(title);
					if (songs.size() > 0) {
						String lyrics = GeniusResource.getLyricsFromSong(songs.get(0));
						if (lyrics != null) {
							cr.setData(lyrics);
						} else {
							cr.setStatus(ClientResponseStatus.NOT_FOUND);
						}
					} else {
						cr.setStatus(ClientResponseStatus.NOT_FOUND);
					}
				} else {
					cr.setStatus(ClientResponseStatus.BAD_REQUEST);
				}
			} else {
				cr.setStatus(ClientResponseStatus.FORBIDDEN);
			}
		} else {
			cr.setStatus(ClientResponseStatus.UNAUTHORIZED);
		}
		
		cr.writeTo(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
