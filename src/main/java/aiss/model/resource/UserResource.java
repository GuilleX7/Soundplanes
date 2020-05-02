package aiss.model.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import aiss.model.soundplanes.User;

public class UserResource {
	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());
	private static UserResource instance;
	
	private Map<UUID, User> users;
	private Map<String, UUID> uuidByFacebookId;
	private Map<String, UUID> uuidBySpotifyId;
	
	private UserResource() {
		users = new HashMap<UUID, User>();
		uuidByFacebookId = new HashMap<String, UUID>();
		uuidBySpotifyId = new HashMap<String, UUID>();
	}
	
	public static UserResource getInstance() {
		if (instance == null) {
			instance = new UserResource();
		}
		
		return instance;
	}
	
	public List<User> getUsers() {
		return new ArrayList<User>(users.values());
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
	
	public Boolean indexUserByFacebookId(User user, String facebookId) {
		if (!uuidByFacebookId.containsKey(facebookId)) {
			uuidByFacebookId.put(facebookId, user.getUUID());
			return true;
		}
		
		return false;
	}
	
	public Boolean indexUserBySpotifyId(User user, String spotifyId) {
		if (!uuidBySpotifyId.containsKey(spotifyId)) {
			uuidBySpotifyId.put(spotifyId, user.getUUID());
			return true;
		}
		
		return false;
	}
	
	public User getUserByFacebookId(String facebookId) {
		User result = null;
		UUID uuid = uuidByFacebookId.getOrDefault(facebookId, null);
		if (uuid != null) {
			result = getUser(uuid);
		}
		return result;
	}
	
	public User getUserBySpotifyId(String spotifyId) {
		User result = null;
		UUID uuid = uuidBySpotifyId.getOrDefault(spotifyId, null);
		if (uuid != null) {
			result = getUser(uuid);
		}
		return result;
	}
}
