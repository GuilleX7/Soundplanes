package aiss.model.resource;

import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import aiss.model.facebook.UserProfile;


public class FacebookResource_JUnitTest {
	
	
	@Test
	public void getUserProfileTest() throws UnsupportedEncodingException {
		String access_token="lgefjhge";
		FacebookResource userFb = FacebookResource.fromToken(access_token);
		UserProfile userFbResults= userFb.getUserProfile();
		assertNull("The search doesn't returned null", userFbResults);	
	}
	

}
