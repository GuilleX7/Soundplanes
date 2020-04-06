package aiss.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiss.model.resource.CountryStatesResource;

/**
 * Servlet implementation class chooseLocationController
 */
public class LocationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Map<String,List<String>> states;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LocationController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String nombre = request.getParameter("name");
		if (nombre == null || nombre.isEmpty()) {
			request.getRequestDispatcher("/registerUser").forward(request, response);
		} else {
			String country = request.getLocale().getDisplayCountry(Locale.US);
			request.setAttribute("country", country);
			List<String> states = CountryStatesResource.getStates().get(country);
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
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	@Override
	public void init() throws ServletException {
		
	}

}
