package aiss.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiss.model.resource.CountryStatesResource;

/**
 * Servlet implementation class chooseLocationController
 */

public class LocationController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LocationController() {
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
		
		String nombre = request.getParameter("name");
		if (nombre == null || nombre.isEmpty()) {
			response.sendRedirect("/registerUser");
		} else {
			String country = request.getLocale().getDisplayCountry(Locale.US);
			request.setAttribute("country", country);
			List<String> states = CountryStatesResource.getStates(country);
			request.setAttribute("states", states);
			request.getRequestDispatcher("/WEB-INF/views/chooseLocation.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
