package aiss.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import aiss.model.resource.GeocodingResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.User;
import aiss.model.geocoding.Location;

/**
 * Servlet implementation class RegisterUserController
 */

public class RegisterUserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterUserController() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getSession().getAttribute("UUID") != null) {
			response.sendRedirect("/map");
			return;
		}
		request.getRequestDispatcher("WEB-INF/views/register.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		if (session.getAttribute("UUID") != null) {
			response.sendRedirect("/map");
			return;
		}
		
		String name = request.getParameter("name"), location = request.getParameter("location"),
				country = request.getParameter("country"), state = request.getParameter("state");

		Location geolocation = null;
		
		if (name == null || name.isEmpty()) {
			doGet(request, response);
			return;
		}
		
		if (country != null && !country.isEmpty() && state != null && !state.isEmpty()) {
			List<Location> locations = GeocodingResource.geocodeCountryState(state, country);
			if (locations.size() < 1) {
				doGet(request, response);
				return;
			}
			geolocation = locations.get(0);
		} else if (location != null && !location.isEmpty()) {
			try {
				geolocation = Location.fromFormat(location);
			} catch (Exception e) {
				doGet(request, response);
				return;
			}
		}
		
		User user = User.of(name, geolocation);
		
		String spotifyId = (String) session.getAttribute("Spotify-id");
		String facebookId = (String) session.getAttribute("Facebook-id");
		
		if (spotifyId != null) { // User wanted to link Spotify id
			if (UserResource.getUserBySpotifyId(spotifyId) == null) { // If nothing holds that Id yet
				user.setSpotifyId(spotifyId); // Link Id with user
				session.removeAttribute("Spotify-id");
			}
		}
		
		if (facebookId != null) { // User wanted to link Facebook id
			if (UserResource.getUserByFacebookId(facebookId) == null) { // If nothing holds that Id yet
				user.setFacebookId(facebookId); // Link Id with user
				session.removeAttribute("Facebook-id");
			}
		}
		
		UserResource.registerUser(user);
		session.setAttribute("UUID", user.getUuid());
		response.sendRedirect("/map");
		return;
	}

}
