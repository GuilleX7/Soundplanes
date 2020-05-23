package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import aiss.model.geocoding.Location;
import aiss.model.ircchat.Channel;
import aiss.model.ircchat.Rol;
import aiss.model.soundplanes.User;

public class IrcChatResource_JUnitTest {
	@Test
	public void createChannelRandomSecretTest() throws UnsupportedEncodingException {
		Channel channelResults = IrcChatResource.createChannel();
		
		assertNotNull("The search returned null", channelResults);
	}

	@Test
	public void createChannelDefinedSecretTest() throws UnsupportedEncodingException {
		final String secret = "1";
		Channel channelResults = IrcChatResource.createChannel(secret);

		assertNotNull("The search returned null", channelResults);
		assertFalse("There is no secret", channelResults.getSecret().length() == 0);
	}

	@Test
	public void createTokenTest() throws UnsupportedEncodingException {
		final String secret = "1";
		final Channel channel = IrcChatResource.createChannel(secret);
		final User user = User.of("Pepe", Location.ofCoordinates(40.4165001, -3.7025599));
		final List<Rol> roles = new ArrayList<>();
		roles.add(Rol.EDIT);
		final Integer expirationSeconds = 1000;
		String tokenResults = IrcChatResource.createToken(channel, user, roles, expirationSeconds);

		assertNotNull("The search returned null", tokenResults);
		assertFalse("There is no token", tokenResults.length() == 0);
	}

	@Test
	public void invalidateTokenTest() throws UnsupportedEncodingException {
		String token = "jgygahwegf";
		Boolean success = IrcChatResource.invalidateToken(token);
		
		assertTrue("Returned false",success);
	}
}
