package aiss.model.ircchat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRequest {
	private String secret;
	private Payload payload;
	private Options options;
	
	public static TokenRequest of(String secret, Payload payload, Options options) {
		return new TokenRequest(secret, payload, options);
	}
	
	private TokenRequest(String secret, Payload payload, Options options) {
		super();
		this.secret = secret;
		this.payload = payload;
		this.options = options;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}
}
