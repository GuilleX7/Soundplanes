package aiss.model.ircchat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {
	private String id;
	private String secret;
	
	public static Channel of(String id, String secret) {
		return new Channel(id, secret);
	}
	
	private Channel(String id, String secret) {
		this.id = id;
		this.secret = secret;
	}
	
	private Channel() {
		this.id = null;
		this.secret = null;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
}
