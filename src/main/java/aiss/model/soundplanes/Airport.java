package aiss.model.soundplanes;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import aiss.model.ircchat.Channel;
import aiss.model.resource.IrcChatResource;
import aiss.model.resource.UserResource;
import aiss.model.spotify.Playlist;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Cache
public class Airport {
	@Id
	private String uuid;
	@JsonIgnore
	private Ref<User> owner;
	@JsonIgnore
	private Channel channel;
	@Index
	private Long creationTimestamp;
	private Playlist playlistInfo;
	
	public static Airport of(String ownerUuid) {
		return new Airport(ownerUuid, UserResource.getUser(ownerUuid), IrcChatResource.createChannel(UUID.randomUUID().toString()), Instant.now().getEpochSecond(), null);
	}
	
	public static Airport of(User owner) {
		return new Airport(owner.getUuid(), owner, IrcChatResource.createChannel(UUID.randomUUID().toString()), Instant.now().getEpochSecond(), null);
	}
	
	private Airport(String uuid, User owner, Channel channel, Long creationTimestamp, Playlist playlistInfo) {
		this.uuid = uuid;
		this.setOwner(owner);
		this.channel = channel;
		this.creationTimestamp = creationTimestamp;
		this.playlistInfo = playlistInfo;
	}
	
	private Airport() {
		this.uuid = null;
		this.owner = null;
		this.creationTimestamp = null;
		this.playlistInfo = null;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public User getOwner() {
		return this.owner.get();
	}

	public void setOwner(User owner) {
		this.owner = Ref.create(owner);
	}

	public Long getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Playlist getPlaylistInfo() {
		return playlistInfo;
	}

	public void setPlaylistInfo(Playlist playlistInfo) {
		this.playlistInfo = playlistInfo;
	}
	
	public Boolean isPlaylistLoaded() {
		return this.playlistInfo != null;
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

	@Override
	public String toString() {
		return "Airport [uuid=" + uuid + ", owner=" + owner + ", channel=" + channel + ", creationTimestamp="
				+ creationTimestamp + ", playlistInfo=" + playlistInfo + "]";
	}
}
