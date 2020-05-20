package aiss.model.ircchat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Secret {
	private String secret;

	public static Secret of(String secret) {
		return new Secret(secret);
	}
	
	private Secret(String secret) {
		super();
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
