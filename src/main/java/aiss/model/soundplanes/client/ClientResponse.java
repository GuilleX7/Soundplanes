package aiss.model.soundplanes.client;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientResponse {
	private ClientResponseStatus status;
	private Object data;

	private ClientResponse(ClientResponseStatus status, Object data) {
		this.status = status;
		this.data = data;
	}

	public static ClientResponse of(ClientResponseStatus status, Object data) {
		return new ClientResponse(status, data);
	}

	public static ClientResponse create() {
		return new ClientResponse(ClientResponseStatus.OK, null);
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setStatus(ClientResponseStatus status) {
		this.status = status;
	}

	public ClientResponseStatus getStatus() {
		return status;
	}

	public void writeTo(HttpServletResponse response) throws IOException {
		customWriteTo(response, new ObjectMapper());
	}
	
	public void customWriteTo(HttpServletResponse response, ObjectMapper customOm) throws IOException {
		response.setContentType("application/json");
		response.setStatus(this.getStatus().getCode());

		try {
			customOm.writeValue(response.getWriter(), this.getData());
		} catch (Exception e) {
			response.setStatus(ClientResponseStatus.INTERNAL_ERROR.getCode());
		}

		response.flushBuffer();
	}
}
