package aiss.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiss.model.resource.GeocodingResource;
import aiss.model.resource.UserResource;
import aiss.model.geocoding.Location;
import aiss.model.user.User;

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
		request.getRequestDispatcher("WEB-INF/views/register.html").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getSession().getAttribute("UUID") != null) {
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
		UserResource.getInstance().registerUser(user);
		request.getSession().setAttribute("UUID", user.getUUID());
		response.sendRedirect("/map");
		return;
	}

}
