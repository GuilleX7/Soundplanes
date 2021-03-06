package aiss.model.soundplanes.client;

public enum ClientResponseStatus {
	OK (200),
	CREATED (201),
	ACCEPTED (202),
	NO_CONTENT (204),
	MOVED_PERMANENTLY (301),
	SEE_OTHER (303),
	BAD_REQUEST (400),
	UNAUTHORIZED (401),
	FORBIDDEN (403),
	NOT_FOUND (404),
	INTERNAL_ERROR (500),
	NOT_IMPLEMENTED (501);

	private int code;

	ClientResponseStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
