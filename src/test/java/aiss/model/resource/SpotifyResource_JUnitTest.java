package aiss.model.resource;

import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;


import org.junit.Test;

import aiss.model.facebook.UserProfile;


public class SpotifyResource_JUnitTest {
	@Test
	public void getUserProfileTest() throws UnsupportedEncodingException {
		String access_token="lgefjhge";
		FacebookResource userSp = FacebookResource.fromToken(access_token);
		UserProfile userSpResults= userSp.getUserProfile();
		
		assertNull("The search doesn't returned null", userSpResults);	
	}
	
	

}
