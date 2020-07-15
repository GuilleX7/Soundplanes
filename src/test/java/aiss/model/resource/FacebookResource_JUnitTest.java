package aiss.model.resource;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Ignore;
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
	final static String token = "EAAl1We7h4BEBAMq9rXSZAS6h3sIsE4loKZBp64aBz96SvyrgNUfXuFg6n50jh2W7q1DjpuYNLO7gsQD1kqWmZBDiQrpJfsl7IqyxOopH3VJ2ILXNY2jUz8O9FFvIjqSNn1G6R8CHChMMci7j2iEf9lIdkLsEOR2mxnm2nTpSp3Sj27gtlvzh3wio1Yw2k6sjFa4XOJnZCtSNaij8RMSh";
	/* END OF MANUAL DATA */
	
	@Ignore
	@Test
	public void getUserProfileTest() throws ResourceException, IOException {
		UserProfile userProfileResults = FacebookResource.fromToken(token).getUserProfile();

		assertNotNull("Returned null", userProfileResults);
	}
}
