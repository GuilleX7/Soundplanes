package aiss.model.client;

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
}
