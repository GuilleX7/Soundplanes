package aiss.controller.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import aiss.model.resource.AirportResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientAirportSerializer;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;

/**
 * Servlet implementation class CreateAirportCountry
 */
public class TestCreateAirportCountryController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestCreateAirportCountryController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
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

		Airport airport = AirportResource.getAirport(user.getUuid());
		if (airport != null) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
			return;
		}

		airport = Airport.of(user);
		AirportResource.registerAirport(airport);
		AirportPlaylist airportPlaylist = AirportPlaylist.empty(airport.getUuid());
		AirportResource.registerAirportPlaylist(airportPlaylist);

		cr.setStatus(ClientResponseStatus.CREATED);
		List<Airport> result = new ArrayList<Airport>();
		result.add(airport);
		cr.setData(result);

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule("SimpleModule");
		simpleModule.addSerializer(Airport.class, ClientAirportSerializer.create());
		mapper.registerModule(simpleModule);
		cr.customWriteTo(response, mapper);
	}

}
