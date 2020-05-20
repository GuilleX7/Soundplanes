package aiss.controller.client;

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
 * Servlet implementation class ClientAirportsController
 */
public class ClientAirportsController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClientAirportsController() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ClientResponse cr = ClientResponse.create();

		List<Airport> airports = new ArrayList<Airport>();
		String uuid = (String) request.getSession().getAttribute("UUID");
		if (uuid == null) {
			cr.setStatus(ClientResponseStatus.UNAUTHORIZED);
			cr.writeTo(response);
			return;
		}

		String query = request.getParameter("query");
		if (query == null) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
		}

		if (query.contentEquals("me")) {
			Airport airport = AirportResource.getAirport(uuid);
			if (airport == null) {
				cr.setStatus(ClientResponseStatus.NOT_FOUND);
				cr.writeTo(response);
				return;
			}
				
			airports.add(airport);
			cr.setStatus(ClientResponseStatus.OK);
		} else if (query.contentEquals("all")) {
			Long lastTimestamp;
			try {
				lastTimestamp = Long.valueOf(request.getParameter("lastTimestamp"));
				if (lastTimestamp.longValue() == 0) {
					airports = AirportResource.getAllAirports();
				} else {
					airports = AirportResource.getAirportsAfter(lastTimestamp);
				}
			} catch (Exception e) {
				cr.setStatus(ClientResponseStatus.BAD_REQUEST);
				cr.writeTo(response);
				return;
			}
		} else {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
			return;
		}

		cr.setStatus(ClientResponseStatus.OK);
		cr.setData(airports);

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule("SimpleModule");
		simpleModule.addSerializer(Airport.class, ClientAirportSerializer.create());
		mapper.registerModule(simpleModule);
		cr.customWriteTo(response, mapper);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
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
			cr.setStatus(ClientResponseStatus.FORBIDDEN);
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