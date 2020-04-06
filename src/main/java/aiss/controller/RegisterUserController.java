package aiss.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		if (location != null && !location.isEmpty()) {
			try {
				String[] coords = location.split(",");
				Double latitude = Double.valueOf(coords[0]);
				Double longitude = Double.valueOf(coords[1]);
			} catch (Exception e) {
				doGet(request, response);
				return;
			}

			response.getWriter().write("Username: " + name + "\nPosition: " + location);
			return;
		}

		if (country != null && !country.isEmpty() && state != null && !state.isEmpty()) {
			response.getWriter().write("Username: " + name + "\nCountry: " + country + "\nState: " + state);
			return;
		}
		
		doGet(request, response);
	}

}
