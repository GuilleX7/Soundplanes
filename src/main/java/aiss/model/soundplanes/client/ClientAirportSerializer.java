package aiss.model.soundplanes.client;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import aiss.model.soundplanes.Airport;

public class ClientAirportSerializer extends StdSerializer<Airport> {

	private static final long serialVersionUID = 1L;

	public static ClientAirportSerializer create() {
		return new ClientAirportSerializer(null);
	}

	public ClientAirportSerializer(Class<Airport> t) {
		super(t);
	}

	@Override
	public void serialize(Airport value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("uuid", value.getUuid());
		gen.writeStringField("name", String.format("%s's airport", value.getOwner().getName()));
		gen.writeNumberField("creationTimestamp", value.getCreationTimestamp());
		gen.writeObjectField("geolocation", value.getOwner().getGeolocation());
		gen.writeEndObject();
	}

}
