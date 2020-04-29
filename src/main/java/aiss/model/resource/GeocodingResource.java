package aiss.model.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import aiss.model.geocoding.GeocodingRequest;
import aiss.model.geocoding.GeocodingResult;
import aiss.model.geocoding.Location;

public class GeocodingResource {
	private static Logger log = Logger.getLogger(GeocodingResource.class.getName());
	
	private static final String API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
	private static final String API_KEY = "AIzaSyBA1H2O2ItX1BXonfYkHPo1HrGol5igLwo";

	public static List<Location> geocodeAddress(String address) {
		List<Location> locations = new ArrayList<>();
		
		ClientResource cr = new ClientResource(API_URL);
		cr.addQueryParameter("address", address);
		cr.addQueryParameter("key", API_KEY);
		GeocodingRequest request = null;
		
		try {
			request = cr.get(GeocodingRequest.class);
		} catch (ResourceException re) {
			log.warning(re.getMessage());
			return locations;
		}
		
		if (!request.getStatus().contentEquals("OK")) {
			log.warning("Geocoding request failed. Status: " + request.getStatus());
			return locations;
		}
	
		for (GeocodingResult result : request.getResults()) {
			locations.add(result.getGeometry().getLocation());
		}
		
		return locations;
	}
	
	public static List<Location> geocodeCountryState(String state, String country) {
		return geocodeAddress(String.format("%s, %s", state, country));
	}
}
