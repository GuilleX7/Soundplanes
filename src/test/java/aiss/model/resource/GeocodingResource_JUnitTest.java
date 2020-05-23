package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;

import aiss.model.geocoding.Location;

public class GeocodingResource_JUnitTest {
	@Test
	public void geocodeAddressTest() throws UnsupportedEncodingException {
		String address = "Spain";
		List<Location> geocodingResults = GeocodingResource.geocodeAddress(address);

		assertNotNull("The search returned null", geocodingResults);
		assertFalse("There are no location in: " + address, geocodingResults.isEmpty());
	}

	@Test
	public void geocodeCountryStateTest() throws UnsupportedEncodingException {
		String state = "Seville";
		String country = "Spain";
		List<Location> geocodingResults = GeocodingResource.geocodeCountryState(state, country);

		assertNotNull("The search returned null", geocodingResults);
		assertFalse("There are no location in: " + state + "," + country + ".", geocodingResults.isEmpty());
	}
}
