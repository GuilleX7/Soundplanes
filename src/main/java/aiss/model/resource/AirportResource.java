package aiss.model.resource;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import aiss.model.soundplanes.Airport;

public class AirportResource {
	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());
	
	public static List<Airport> getAirports() {
		return ofy().load().type(Airport.class).list();
	}
	
	public static Airport getAirport(String uuid) {
		return ofy().load().type(Airport.class).id(uuid).now();
	}
	
	public static void registerAirport(Airport airport) {
		ofy().save().entity(airport);
		log.info(String.format("Airport with UUID %s registered successfully with owner UUID %s", airport.getUUID(), airport.getOwnerUuid()));
	}
	
	public static void indexAirportByUser(String uuid, String userUuid) {
		Airport airport = ofy().load().type(Airport.class).id(uuid).now();
		if (airport != null) {
			airport.setOwner(userUuid);
			ofy().save().entity(airport);
		}
	}
	
	public static Airport getAirportByUser(String userUuid) {
		return ofy().load().type(Airport.class).filter("ownerUuid", userUuid).first().now();
	}
}
