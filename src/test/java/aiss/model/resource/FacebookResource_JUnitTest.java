package aiss.model.resource;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.facebook.UserProfile;

public class FacebookResource_JUnitTest {
	/* CHANGE THESE VARIABLES IN ORDER TO EXECUTE THE TEST
	 * TO OBTAIN A VALID TOKEN, GO TO
	 * https://developers.facebook.com/tools/explorer/
	 * LOG IN, SELECT 'Soundplanes' and 'User Identifier',
	 * AND 'public_profile' AS SCOPE
	 * ((NOTE: YOU MAY NEED TO BE ADDED AS DEVELOPER OF
	 *         THE PROJECT))
	 * CLICK 'Generate Access Token'
	 * THEN CHANGE token HERE
	 */
	/* START OF MANUAL DATA */
	final static String token = "EAAl1We7h4BEBADqJBnlGdxH7m4v8xEfxxTMRqgxliDipKOMT2ZC0saPqJSQsZCrtZB0lG2kZCORMaUTsGxhBn9J3wl18pQAy8E9vynIq5gQCNqwJyPcy9yOVwLp86pgZA6hbPUJrQcc2zGDQLjYKHQxq71uci5F1zEkXq3ooploLXZAkh6NnOvqnMbuhAtHfFm4TwTIBqTmXMw9FIpE9GxCv2eTVZBDojIGvoemZBITgvAZDZD";
	/* END OF MANUAL DATA */
	
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		UserProfile userProfileResults = FacebookResource.fromToken(token).getUserProfile();

		assertNotNull("Returned null", userProfileResults);
	}
}
