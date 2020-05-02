package aiss.model.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.User;

public class AirportResource {
	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());
	private static AirportResource instance;
	
	private Map<UUID, Airport> airports;
	private Map<User, UUID> uuidByUser;
	
	private AirportResource() {
		airports = new HashMap<UUID, Airport>();
		uuidByUser = new HashMap<User, UUID>();
	}
	
	public static AirportResource getInstance() {
		if (instance == null) {
			instance = new AirportResource();
		}
		
		return instance;
	}
	
	public List<Airport> getAirports() {
		return new ArrayList<Airport>(airports.values());
	}
	
	public Airport getAirport(UUID uuid) {
		return airports.getOrDefault(uuid, null);
	}
	
	public Boolean existsAirport(UUID uuid) {
		return airports.containsKey(uuid);
	}
	
	public void registerAirport(Airport airport) {
		airports.put(airport.getUUID(), airport);
		log.info(String.format("Airport with UUID %s registered successfully with owner UUID %s", airport.getUUID(), airport.getOwner().getUUID()));
	}
	
	public Boolean indexAirportByUser(Airport airport, User user) {
		if (!uuidByUser.containsKey(user)) {
			uuidByUser.put(user, airport.getUUID());
			return true;
		}
		
		return false;
	}
	
	public Airport getAirportByUser(User user) {
		Airport result = null;
		UUID uuid = uuidByUser.getOrDefault(user, null);
		if (uuid != null) {
			result = getAirport(uuid);
		}
		return result;
	}
}
