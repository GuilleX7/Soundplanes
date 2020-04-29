package aiss.model.user;

import java.util.UUID;

public class User {
	private UUID uuid;
	private String name;
	private Location geolocation;
	
	public static User of(String name, Location geolocation) {
		return new User(UUID.randomUUID(), name, geolocation);
	}
	
	private User(UUID uuid, String name, Location geolocation) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
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
	
}
