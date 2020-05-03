package aiss.model.resource;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import aiss.model.soundplanes.User;

public class UserResource {
	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());
	
	public static List<User> getUsers() {
		return ofy().load().type(User.class).list();
	}
	
	public static User getUser(String uuid) {
		return ofy().load().type(User.class).id(uuid.toString()).now();
	}

	public static void registerUser(User user) {
		ofy().save().entity(user).now();
		log.info(String.format("User %s with UUID %s registered succesfully", user.getName(), user.getUuid()));
	}
	
	public static void linkUserWithFacebookId(String uuid, String facebookId) {
		User user = ofy().load().type(User.class).id(uuid).now();
		if (user != null) {
			user.setFacebookId(facebookId);
			ofy().save().entity(user);
		}
	}
	
	public static void linkUserWithSpotifyId(String uuid, String spotifyId) {
		User user = ofy().load().type(User.class).id(uuid).now();
		if (user != null) {
			user.setFacebookId(spotifyId);
			ofy().save().entity(user);
		}
	}
	
	public static User getUserByFacebookId(String facebookId) {
		return ofy().load().type(User.class).filter("facebookId", facebookId).first().now();
	}
	
	public static User getUserBySpotifyId(String spotifyId) {
		return ofy().load().type(User.class).filter("spotifyId", spotifyId).first().now();
	}
}
