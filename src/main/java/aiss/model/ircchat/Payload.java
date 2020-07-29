package aiss.model.ircchat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {
	private String sub;
	private String channel;
	private String name;
	private List<Permission> permissions;
	
	public static Payload of(String sub, String channel, String name, List<Permission> permissions) {
		return new Payload(sub, channel, name, permissions);
	}
	
	private Payload(String sub, String channel, String name, List<Permission> permissions) {
		super();
		this.sub = sub;
		this.channel = channel;
		this.name = name;
		this.permissions = permissions;
	}

	public String getSub() {
		return sub;
	}
	
	public void setSub(String sub) {
		this.sub = sub;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
}
