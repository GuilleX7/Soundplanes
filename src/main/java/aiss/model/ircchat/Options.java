package aiss.model.ircchat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Options {
	private String expiresIn;

	public static Options of(String expiresIn) {
		return new Options(expiresIn);
	}
	
	public static Options of(Integer expiresIn) {
		return new Options(String.format("%ds", expiresIn));
	}
	
	private Options(String expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
}
