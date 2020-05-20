package aiss.model.ircchat;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {
	private String error;
	@JsonProperty("success")
	private T data;
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	@JsonGetter("data")
	public T getData() {
		return data;
	}
	
	@JsonSetter("data")
	public void setData(T data) {
		this.data = data;
	}
	
	public static class StringResponse extends Response<String> {}
	public static class TokenResponse extends Response<Token> {}
}
