package aiss.model.soundplanes.client;

public enum ClientResponseStatus {
	OK (0),
	NOT_LOGGED (1),
	NOT_ALLOWED (2),
	BAD_LOGIN (3),
	INTERNAL_ERROR (4);

	private int code;

	ClientResponseStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
