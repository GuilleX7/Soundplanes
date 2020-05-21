package aiss.model.resource;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import com.google.cloud.datastore.QueryResults;

import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;

public class AirportResource {
	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());
	
	public static List<Airport> getAllAirports() {
		return ofy().load().type(Airport.class).list();
	}
	
	public static List<Airport> getAirportsAfter(Long queryTimestamp ) {
		return ofy().load().type(Airport.class).filter("creationTimestamp >", queryTimestamp).list();
	}
	
	public static Airport getAirport(String uuid) {
		return ofy().load().type(Airport.class).id(uuid).now();
	}
	
	public static void registerAirport(Airport airport) {
		ofy().save().entity(airport).now();
		log.info(String.format("Airport with UUID %s registered successfully with owner UUID %s", airport.getUuid(), airport.getOwner().getUuid()));
	}
	
	public static void removeAllAirports() {
		QueryResults<Airport> cursor = ofy().load().type(Airport.class).iterator();
		while (cursor.hasNext()) {
			ofy().delete().entity(cursor.next()).now();
		}
	}
	
	public static AirportPlaylist getAirportPlaylist(String airportUuid) {
		return ofy().load().type(AirportPlaylist.class).id(airportUuid).now();
	}
	
	public static void registerAirportPlaylist(AirportPlaylist airportPlaylist) {
		ofy().save().entity(airportPlaylist).now();
		log.info(String.format("AirportPlaylist with UUID %s register succesfully", airportPlaylist.getUuid()));
	}
	
	public static void removeAllAirportPlaylists() {
		QueryResults<AirportPlaylist> cursor = ofy().load().type(AirportPlaylist.class).iterator();
		while (cursor.hasNext()) {
			ofy().delete().entity(cursor.next()).now();
		}
	}
	
}
