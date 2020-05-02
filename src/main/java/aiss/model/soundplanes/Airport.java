package aiss.model.soundplanes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import aiss.model.geocoding.Location;
import aiss.model.spotify.Track;

public class Airport {
	private UUID uuid;
	private User owner;
	@JsonIgnore(true)
	private List<Track> playlist;
	private Location location;
	
	public static Airport of(String name, Location location, User owner, List<Track> playlist) {
		return new Airport(UUID.randomUUID(), location, owner, playlist);
	}
	
	public static Airport of(String name, Location location, User owner) {
		return new Airport(UUID.randomUUID(), location, owner, new ArrayList<Track>());
	}
	
	public Airport(UUID uuid, Location location, User owner, List<Track> playlist) {
		super();
		this.uuid = uuid;
		this.owner = owner;
		this.playlist = playlist;
		this.location = location;
	}

	public UUID getUUID() {
		return uuid;
	}
	
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public List<Track> getPlaylist() {
		return playlist;
	}
	
	public void setPlaylist(List<Track> playlist) {
		this.playlist = playlist;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
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
		Airport other = (Airport) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
