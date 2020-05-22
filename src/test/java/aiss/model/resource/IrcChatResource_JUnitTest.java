package aiss.model.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
	public void createChannelTest1() throws UnsupportedEncodingException {
		IrcChatResource channel= new IrcChatResource();
		Channel channelResults= channel.createChannel();
		
		assertNotNull("The search returned null", channelResults);
	}
	
	@Test
	public void createChannelTest2() throws UnsupportedEncodingException {
		String secret= "1";
		IrcChatResource channel= new IrcChatResource();
		Channel channelResults= channel.createChannel(secret);
		
		assertNotNull("The search returned null", channelResults);
		assertFalse("There is no secret", channelResults.getSecret().length()==0);
	}
	
	@Test
	public void createTokenTest() throws UnsupportedEncodingException {
		String secret= "1";
		Channel channel= IrcChatResource.createChannel(secret);
		User user= User.of("Pepe", Location.ofCoordinates(40.4165001, -3.7025599));
		Rol rol= Rol.EDIT;
		List<Rol> roles= new ArrayList<>();
		roles.add(rol);
		Integer expirationSeconds = 1000;
		String tokenResults= IrcChatResource.createToken(channel, user, roles, expirationSeconds);
		
		assertNotNull("The search returned null", tokenResults);
		assertFalse("There is no token", tokenResults.length()==0);
	}
	
	
	@Test
	public void invalidateToken() throws UnsupportedEncodingException {
		String token= "jgygahwegf";
		Boolean invalidTokenResults= IrcChatResource.invalidateToken(token);
	}
	
	

}
