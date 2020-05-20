package aiss.model.ircchat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {
	private String token;
	
	public static Token of(String token) {
		return new Token(token);
	}
	
	private Token(String token) {
		this.setToken(token);
	}
	
	private Token() {
		this.setToken(null);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
