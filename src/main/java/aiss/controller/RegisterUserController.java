package aiss.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;

import aiss.model.genius.SearchRequest;
import aiss.model.genius.Song;
import aiss.model.resource.GeniusResource;
import aiss.model.resource.GeocodingResource;
import aiss.model.resource.UserResource;
import aiss.model.user.Location;
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
		request.getRequestDispatcher("WEB-INF/views/register.html").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name"), location = request.getParameter("location"),
				country = request.getParameter("country"), state = request.getParameter("state");

		if (name == null || name.isEmpty()) {
			doGet(request, response);
			return;
		}
		
		if (country != null && !country.isEmpty() && state != null && !state.isEmpty()) {
			response.getWriter().write("Username: " + name + "\nCountry: " + country + "\nState: " + state);
			return;
		}

		if (location != null && !location.isEmpty()) {
			Location geolocation = null;
			try {
				geolocation = Location.fromFormat(location);
			} catch (Exception e) {
				doGet(request, response);
				return;
			}
			User user = User.of(name, geolocation);
			UserResource.getInstance().registerUser(user);
			request.getSession().setAttribute("UUID", user.getUUID());
			response.getWriter().write("User registered successfully");
			return;
		}
	}

}
