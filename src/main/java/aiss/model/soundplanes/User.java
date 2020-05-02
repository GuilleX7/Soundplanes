package aiss.model.soundplanes;

import java.util.UUID;

import aiss.model.geocoding.Location;

public class User {
	private UUID uuid;
	private String name;
	private Location geolocation;
	private String spotifyId;
	private String facebookId;
	
	public static User of(String name, Location geolocation) {
		return new User(UUID.randomUUID(), name, geolocation, null, null);
	}
	
	private User(UUID uuid, String name, Location geolocation, String spotifyId, String facebookId) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
		this.setSpotifyId(spotifyId);
		this.setFacebookId(facebookId);
	}
	
	public UUID getUUID() {
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((facebookId == null) ? 0 : facebookId.hashCode());
		result = prime * result + ((geolocation == null) ? 0 : geolocation.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((spotifyId == null) ? 0 : spotifyId.hashCode());
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
