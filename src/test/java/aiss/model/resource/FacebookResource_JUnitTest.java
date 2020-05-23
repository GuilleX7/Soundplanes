package aiss.model.resource;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.restlet.resource.ResourceException;

import aiss.model.facebook.UserProfile;

public class FacebookResource_JUnitTest {
	/* CHANGE THESE VARIABLES IN ORDER TO EXECUTE THE TEST
	 * TO OBTAIN A VALID TOKEN, GO TO
	 * https://developers.facebook.com/tools/explorer/v2/
	 * LOG IN, SELECT "User Identifier" AND "public_profile" AS SCOPE
	 * CLICK "Generate Access Token"
	 * THEN CHANGE token HERE
	 */
	/* START OF MANUAL DATA */
	final static String token = "EAAl1We7h4BEBACoopWZCd4G5E1YXZBZAdYrPM3G7bPsXwwvNJVjWJZBHELhxZCemLTr92JkDkeo8iELPxdBJ5Ay81kiAZAlvPmCJb9EZBZB2RAy0P4hqJIyIqeBYCyyunrWYAsDvyvSyoUm3XNurqGokvmeBZChtCXZAlb5GT43xp5ZCzJDjX3KE3MWu03armmtdIQT7FqA1HpwuiZArOFBvYtmPlh4CGQZA33PtXehKZAOcFe0wZDZD";
	/* END OF MANUAL DATA */
	
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		UserProfile userProfileResults = FacebookResource.fromToken(token).getUserProfile();

		assertNotNull("Returned null", userProfileResults);
	}
}
