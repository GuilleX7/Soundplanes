package aiss.model.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import aiss.model.user.User;

public class UserResource {
	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());
	private static UserResource instance;
	
	private Map<UUID, User> users;
	private Map<String, UUID> uuidByFacebookID;
	private Map<String, UUID> uuidBySpotifyID;
	
	private UserResource() {
		users = new HashMap<UUID, User>();
		uuidByFacebookID = new HashMap<String, UUID>();
		uuidBySpotifyID = new HashMap<String, UUID>();
	}
	
	public static UserResource getInstance() {
		if (instance == null) {
			instance = new UserResource();
		}
		
		return instance;
	}
	
	public User getUser(UUID uuid) {
		return users.getOrDefault(uuid, null);
	}
	
	public Boolean existsUser(UUID uuid) {
		return users.containsKey(uuid);
	}

	public void registerUser(User user) {
		users.put(user.getUUID(), user);
		log.info(String.format("User %s with UUID %s registered succesfully", user.getName(), user.getUUID()));
	}
	
	public User getUserbyFacebookID(String facebookID) {
		User result = null;
		UUID uuid = uuidByFacebookID.getOrDefault(facebookID, null);
		if (uuid != null) {
			result = getUser(uuid);
		}
		return result;
	}
	
	public User getUserbySpotifyID(String spotifyID) {
		User result = null;
		UUID uuid = uuidBySpotifyID.getOrDefault(spotifyID, null);
		if (uuid != null) {
			result = getUser(uuid);
		}
		return result;
	}

}
