package aiss.controller.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import aiss.model.resource.SpotifyResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.User;
import aiss.model.spotify.UserProfile;

/**
 * Servlet implementation class LinkSpotifyController
 */
public class LinkSpotifyController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LinkSpotifyController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String uuid = (String) session.getAttribute("UUID");
		String spotifyToken = (String) session.getAttribute("Spotify-token");
		if (spotifyToken == null || spotifyToken.isEmpty()) {
			response.sendRedirect("/registerUser");
			return;
		}
		UserProfile profile = SpotifyResource.fromToken(spotifyToken).getUserProfile();
		if (profile == null) {
			response.sendRedirect("/registerUser");
			return;
		}
		String spotifyId = profile.getId();
		User linkedUser = UserResource.getUserBySpotifyId(spotifyId); //Get user linked with that Spotify ID
		
		if (linkedUser != null) { // There is already a linked user
			session.setAttribute("UUID", linkedUser.getUuid()); // Log in
			session.removeAttribute("Spotify-id");
			session.removeAttribute("Facebook-id");
			response.sendRedirect("/map");
		} else { // There is not a linked user
			if (uuid != null) { // But user is logged
				UserResource.linkUserWithSpotifyId(uuid, spotifyId); // Link user with that Spotify Id
				response.sendRedirect("/map");
			} else { // And user is not logged
				session.setAttribute("Spotify-id", spotifyId); // Save Spotify Id for future linking
				response.sendRedirect("/registerUser");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
