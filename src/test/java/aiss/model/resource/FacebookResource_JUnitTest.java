package aiss.model.resource;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.facebook.UserProfile;



public class FacebookResource_JUnitTest {
	
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		final String token="";
		UserProfile userProfileResults= FacebookResource.fromToken(token).getUserProfile();
		assertNotNull("Returned null", userProfileResults);
		
	}
	
}
