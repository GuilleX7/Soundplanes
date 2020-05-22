package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;


public class CountryStatesResource_JUnitTest {
	
	@Test
	public void getStatesTest() throws UnsupportedEncodingException {
		String country="Spain";
		List<String> countryFileResults= CountryStatesResource.getStates(country);
		
		assertNotNull("The search returned null", countryFileResults);	
		assertFalse("There are no states in: "+country,  countryFileResults.isEmpty());
	}

}
