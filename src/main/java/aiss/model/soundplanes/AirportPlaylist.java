package aiss.model.soundplanes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import aiss.model.spotify.Track;

@Entity 
@Cache
public class AirportPlaylist {
	@Id
	private String uuid;
	private List<Track> tracks;
	
	public static AirportPlaylist empty(String airportUuid) {
		return new AirportPlaylist(airportUuid, new ArrayList<Track>());
	}
	
	public static AirportPlaylist of(String airportUuid, List<Track> tracks) {
		return new AirportPlaylist(airportUuid, tracks);
	}
	
	private AirportPlaylist(String uuid, List<Track> tracks) {
		super();
		this.uuid = uuid;
		this.tracks = tracks;
	}
	
	private AirportPlaylist() {
		super();
		this.uuid = null;
		this.tracks = null;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}
	
	public Track getRandomTrack() {
		return this.tracks.get(new Random().nextInt(this.tracks.size()));
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
		AirportPlaylist other = (AirportPlaylist) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AirportPlaylist [uuid=" + uuid + ", tracks=" + tracks + "]";
	}
}
