package aiss.model.resource;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import aiss.model.ircchat.Channel;
import aiss.model.ircchat.Options;
import aiss.model.ircchat.Payload;
import aiss.model.ircchat.Response.StringResponse;
import aiss.model.ircchat.Response.TokenResponse;
import aiss.model.ircchat.Rol;
import aiss.model.ircchat.Secret;
import aiss.model.ircchat.TokenRequest;
import aiss.model.soundplanes.User;

public class IrcChatResource {
	private static Logger log = Logger.getLogger(IrcChatResource.class.getName());
	
	private static final String API_URL = "https://chat.meantoplay.games/";
	
	public static Channel createChannel() {
		return createChannel(UUID.randomUUID().toString());
	}
	
	public static Channel createChannel(String secret) {
		ClientResource cr = new ClientResource(String.format("%schannel", API_URL));
		
		Channel channel = null;
		StringResponse response = null;
		
		try {
			response = cr.post(Secret.of(secret), StringResponse.class);
		} catch (ResourceException e) {
			log.warning("Couldn't register a channel with secret " + secret);
			log.warning(e.getLocalizedMessage());
		}
		
		if (response.getError() != null) {
			log.warning("Couldn't register a channel with secret " + secret);
			log.warning(response.getError());
		}
		
		channel = Channel.of(response.getData(), secret);
		
		return channel;
	}
	
	public static String createToken(Channel channel, User user, List<Rol> roles, Integer expirationSeconds) {
		ClientResource cr = new ClientResource(String.format("%stoken", API_URL));
		
		String token = null;
		TokenResponse response = null;
		
		try {
			response = cr.post(TokenRequest.of(channel.getSecret(), Payload.of(user.getUuid(), channel.getId(), user.getName(), Rol.getDefaultUserRoles()), Options.of(3600)), TokenResponse.class);
		} catch (ResourceException e) {
			log.warning("Couldn't create a token for channel ID " + channel.getId());
			log.warning(e.getLocalizedMessage());
		}
		
		if (response.getError() != null) {
			log.warning("Couldn't create a token for channel ID " + channel.getId());
			log.warning(response.getError());
		}
		
		token = response.getData().getToken();
		
		return token;
	}
	
	public static void invalidateToken(String token) {
		ClientResource cr = new ClientResource(String.format("%stoken", API_URL));
		cr.addQueryParameter("token", token);
		
		try {
			cr.delete();
		} catch (ResourceException e) {
			log.warning("Couldn't invalidate token " + token);
		}
		
		return; 
	}
}
