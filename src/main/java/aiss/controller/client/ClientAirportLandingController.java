package aiss.controller.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import aiss.model.ircchat.Permission;
import aiss.model.resource.AirportResource;
import aiss.model.resource.IrcChatResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientAirportLandingSerializer;
import aiss.model.soundplanes.client.ClientAirportSerializer;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;

/**
 * Servlet implementation class ClientAirportVisitController
 */
public class ClientAirportLandingController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientAirportLandingController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		if (!user.isLanded()) {
			cr.setStatus(ClientResponseStatus.NOT_FOUND);
			cr.writeTo(response);
			return;
		}
		
		String airportUuid = user.getLandedOn();
		
		Airport airport = AirportResource.getAirport(airportUuid);
		if (airport == null) {
			cr.setStatus(ClientResponseStatus.INTERNAL_ERROR);
			user.setLandedOn(null);
			if (user.getChatToken() != null) {
				IrcChatResource.invalidateToken(user.getChatToken());
			}
			user.setChatToken(null);
			UserResource.registerUser(user);
			cr.writeTo(response);
			return;
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("user", user);
		data.put("airport", airport);
		cr.setStatus(ClientResponseStatus.OK);
		cr.setData(data);
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule("SimpleModule");
		simpleModule.addSerializer(User.class, ClientAirportLandingSerializer.create());
		simpleModule.addSerializer(Airport.class, ClientAirportSerializer.create());
		mapper.registerModule(simpleModule);
		cr.customWriteTo(response, mapper);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		String airportUuid = request.getParameter("airportUUID");
		if ((user.isLanded() && !user.getLandedOn().contentEquals(airportUuid)) || airportUuid == null) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
			return;
		}
		
		Airport airport = AirportResource.getAirport(airportUuid);
		if (airport == null) {
			cr.setData(airportUuid);
			cr.setStatus(ClientResponseStatus.NOT_FOUND);
			cr.writeTo(response);
			return;
		}
		
		user.setLandedOn(airport.getUuid());
		if (user.getChatToken() != null) {
			IrcChatResource.invalidateToken(user.getChatToken());
		}
		user.setChatToken(IrcChatResource.createToken(airport.getChannel(), user, Permission.getDefaultUserRoles(), 3600));
		UserResource.registerUser(user);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("user", user);
		data.put("airport", airport);
		cr.setStatus(ClientResponseStatus.OK);
		cr.setData(data);
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule("SimpleModule");
		simpleModule.addSerializer(User.class, ClientAirportLandingSerializer.create());
		simpleModule.addSerializer(Airport.class, ClientAirportSerializer.create());
		mapper.registerModule(simpleModule);
		
		cr.customWriteTo(response, mapper);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		if (!user.isLanded()) {
			cr.setStatus(ClientResponseStatus.BAD_REQUEST);
			cr.writeTo(response);
			return;
		}
		
		Airport airport = AirportResource.getAirport(user.getLandedOn());
		if (airport == null) {
			cr.setData(user.getLandedOn());
			cr.setStatus(ClientResponseStatus.NOT_FOUND);
		} else {
			cr.setStatus(ClientResponseStatus.NO_CONTENT);
		}
		
		user.setLandedOn(null);
		if (user.getChatToken() != null) {
			IrcChatResource.invalidateToken(user.getChatToken());
		}
		user.setChatToken(null);
		UserResource.registerUser(user);
		
		cr.writeTo(response);
	}
	
}
