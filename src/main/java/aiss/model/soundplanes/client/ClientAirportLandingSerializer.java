package aiss.model.soundplanes.client;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import aiss.model.soundplanes.User;

public class ClientAirportLandingSerializer extends StdSerializer<User> {

	private static final long serialVersionUID = 1L;

	public static ClientAirportLandingSerializer create() {
		return new ClientAirportLandingSerializer(null);
	}

	public ClientAirportLandingSerializer(Class<User> t) {
		super(t);
	}

	@Override
	public void serialize(User value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("landedOn", value.getLandedOn());
		gen.writeStringField("chatToken", value.getChatToken());
		gen.writeEndObject();
	}

}
