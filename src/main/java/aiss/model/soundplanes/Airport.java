package aiss.model.soundplanes;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import aiss.model.ircchat.Channel;
import aiss.model.resource.IrcChatResource;
import aiss.model.resource.UserResource;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Cache
public class Airport {
	@Id
	private String uuid;
	private Ref<User> owner;
	private Channel channel;
	@Index
	private Long creationTimestamp;
	
	public static Airport of(String ownerUuid) {
		return new Airport(ownerUuid, UserResource.getUser(ownerUuid), IrcChatResource.createChannel(UUID.randomUUID().toString()), Instant.now().getEpochSecond());
	}
	
	public static Airport of(User owner) {
		return new Airport(owner.getUuid(), owner, IrcChatResource.createChannel(UUID.randomUUID().toString()), Instant.now().getEpochSecond());
	}
	
	private Airport(String uuid, User owner, Channel channel, Long creationTimestamp) {
		this.uuid = uuid;
		this.setOwner(owner);
		this.channel = channel;
		this.creationTimestamp = creationTimestamp;
	}
	
	private Airport() {
		this.uuid = null;
		this.owner = null;
		this.creationTimestamp = null;
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
