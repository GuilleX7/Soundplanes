package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import aiss.model.geocoding.Location;
import aiss.model.soundplanes.User;

public class UserResource_JUnitTest {
	
	@Test
	public void getUsersTest() throws UnsupportedEncodingException {
		
	}
	@Test
	public void getUserTest() throws UnsupportedEncodingException {
		String uuid="kjadwkhQDWH";
		UserResource user = new UserResource();
		User userResults= user.getUser(uuid);
		
		assertNotNull("The search returned null", userResults);
		assertFalse("There is no user with that id: ",  userResults.getUuid().length()==0);
	}
	
	@Test
	public void registerUserTest() throws UnsupportedEncodingException {
		User user= User.of("Pepe", Location.ofCoordinates(40.4165001, -3.7025599));
		
	}
	
	@Test
	public void linkUserWithFacebookIdTest() throws UnsupportedEncodingException {
		
	}
	
	@Test
	public void linkUserWithSpotifyIdTest() throws UnsupportedEncodingException {
		
	}

	@Test
	public void getUserByFacebookIdTest() throws UnsupportedEncodingException {
		
	}
	
	@Test
	public void getUserBySpotifyIdTest() throws UnsupportedEncodingException {
		
	}
	
	

}
