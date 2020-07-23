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
	final static String token = "EAAl1We7h4BEBAHZBkBIUMsvaqLtHL4PZAtju8gSZAZAcmMDnwei64dZBaM7PYU8ZBa32BETKfDA1MZBbG9AjUdjIbiL9TleAYzUCfIRn3qZBRulRmThMo6CHX0bQNCQCP29UZBTxos7c5sCcs9MGb7SImJMVCvtxRmV3jUFb5p5x2CCziyNzwbnppWwE8UzxwkiS8y7FrtO7kLla03KKqtRKvwnBTpq1RB34k8uwZCgW5wHAZDZD";
	/* END OF MANUAL DATA */
	
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		UserProfile userProfileResults = FacebookResource.fromToken(token).getUserProfile();

		assertNotNull("Returned null", userProfileResults);
	}
}
