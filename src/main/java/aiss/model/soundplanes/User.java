package aiss.model.soundplanes;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import aiss.model.geocoding.Location;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Cache
public class User {
	@Id
	private String uuid;
	private String name;
	private Location geolocation;
	@Index
	private String spotifyId;
	@Index
	private String facebookId;
	@JsonIgnore
	private String landedOn;
	@JsonIgnore
	private String chatToken;
	
	public static User of(String name, Location geolocation) {
		return new User(UUID.randomUUID().toString(), name, geolocation, null, null, null, null);
	}
	
	public static User of(String uuid, String name, Location geolocation) {
		return new User(uuid, name, geolocation, null, null, null, null);
	}
	
	private User(String uuid, String name, Location geolocation, String spotifyId, String facebookId, String landedOn, String chatToken) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
		this.spotifyId = spotifyId;
		this.facebookId = facebookId;
		this.landedOn = landedOn;
		this.chatToken = chatToken;
	}
	
	private User() {
		super();
		this.uuid = null;
		this.name = null;
		this.geolocation = null;
		this.spotifyId = null;
		this.facebookId = null;
		this.landedOn = null;
		this.chatToken = null;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Location getGeolocation() {
		return geolocation;
	}
	
	public void setGeolocation(Location geolocation) {
		this.geolocation = geolocation;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	
	public String getLandedOn() {
		return landedOn;
	}

	public void setLandedOn(String landedOn) {
		this.landedOn = landedOn;
	}
	
	@JsonIgnore
	public Boolean isLanded() {
		return this.landedOn != null;
	}

	public String getChatToken() {
		return chatToken;
	}

	public void setChatToken(String chatToken) {
		this.chatToken = chatToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [uuid=" + uuid + ", name=" + name + ", geolocation=" + geolocation + ", spotifyId=" + spotifyId
				+ ", facebookId=" + facebookId + "]";
	}
}
