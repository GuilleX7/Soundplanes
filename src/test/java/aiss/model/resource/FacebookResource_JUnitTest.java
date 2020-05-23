package aiss.model.resource;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.facebook.UserProfile;

public class FacebookResource_JUnitTest {
	final static String token = "EAAl1We7h4BEBAPZBHGKe4rRZBZAuhaoNUFZAclORahDNo7fT0J5J38ZCyXN6S7LNZBxUbF1wF72tDxl1YJjpvSjjcglQ9zepfLoJN8kOmCu4bmInDxhvZBabT1auPSZBBqCSbgtPt2uMzg0vU6xfBMp3VKZChoIWTZCR75Y5izmonNAxTS3zZBJ15rL4ZAwlqyT8Q6JDLau03JPKqFSWHbsJ3YpF";
	
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		UserProfile userProfileResults = FacebookResource.fromToken(token).getUserProfile();

		assertNotNull("Returned null", userProfileResults);
	}
}
