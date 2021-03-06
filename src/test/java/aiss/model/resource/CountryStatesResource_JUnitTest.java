package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;

public class CountryStatesResource_JUnitTest {
	@Test
	public void getStatesTest() throws UnsupportedEncodingException {
		final String country = "Spain";
		List<String> countryFileResults = CountryStatesResource.getStates(country);

		assertNotNull("The search returned null", countryFileResults);
		assertFalse("There are no states in: " + country, countryFileResults.isEmpty());
	}
	
	@Test
	public void getInexistentStateTest() throws UnsupportedEncodingException {
		final String country = "invented";
		List<String> states = CountryStatesResource.getStates(country);
		
		assertTrue("There exist a state for inexistent country", states.isEmpty());
	}
}
