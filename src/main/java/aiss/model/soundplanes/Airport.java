package aiss.model.soundplanes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import aiss.model.geocoding.Location;
import aiss.model.spotify.Track;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Cache
public class Airport {
	@Id
	private String uuid;
	@Index
	private String ownerUuid;
	@JsonIgnore(true)
	private List<Track> playlist = new ArrayList<Track>();
	private Location location;
	
	public static Airport of(String name, Location location, String ownerUuid, List<Track> playlist) {
		return new Airport(UUID.randomUUID().toString(), location, ownerUuid, playlist);
	}
	
	public static Airport of(String name, Location location, String ownerUuid) {
		return new Airport(UUID.randomUUID().toString(), location, ownerUuid, new ArrayList<Track>());
	}
	
	private Airport(String uuid, Location location, String ownerUuid, List<Track> playlist) {
		this.uuid = uuid;
		this.ownerUuid = ownerUuid;
		this.playlist = playlist;
		this.location = location;
	}
	
	private Airport() {
		this.uuid = null;
		this.ownerUuid = null;
		this.playlist = null;
		this.location = null;
	}

	public String getUUID() {
		return uuid;
	}
	
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	
	public String getOwnerUuid() {
		return ownerUuid;
	}
	
	public void setOwner(String ownerUuid) {
		this.ownerUuid = ownerUuid;
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
