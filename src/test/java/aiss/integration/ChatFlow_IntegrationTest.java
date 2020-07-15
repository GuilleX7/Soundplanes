package aiss.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import aiss.model.geocoding.Location;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.User;

public class ChatFlow_IntegrationTest {
	private static final Logger log = Logger.getLogger(UserFlow_IntegrationTest.class.getName());
	final String baseUri = "https://localhost";

	@Ignore
	@Test
	public void ChatFlowTest() throws IOException {
		HttpPost createUser = new HttpPost(baseUri + "/registerUser");
		String name = "Test";
		String location = "37.2577157,-6.5036537";
		List<BasicNameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("name", name));
		urlParameters.add(new BasicNameValuePair("location", location));

		createUser.setEntity(new UrlEncodedFormEntity(urlParameters));
		String sessionId = null;
		log.info("Creating user " + name + " with location: " + location);
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpClient.execute(createUser);
			sessionId = response.getFirstHeader("Set-Cookie").getValue().split(";")[0];
			assertEquals(302, response.getStatusLine().getStatusCode());
		}
		assertNotNull(sessionId);
		log.info("User created successfully");
		HttpGet getUser = new HttpGet(baseUri + "/client/user/profile");
		getUser.addHeader("Cookie", sessionId);
		User user = null;
		log.info("Reading user info");
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpClient.execute(getUser);
			assertEquals(200, response.getStatusLine().getStatusCode());
			ObjectMapper om = new ObjectMapper();
			user = om.readValue(response.getEntity().getContent(), User.class);
		}
		assertNotNull(user);
		assertTrue(Location.fromFormat(location).equals(user.getGeolocation()));
		assertTrue(name.equals(user.getName()));
		assertNull(user.getSpotifyId());
		log.info("User retrieved successfully");

		log.info("Creating mock airport");
		HttpPost createAirport = new HttpPost(baseUri + "/createTestAirport");
		createAirport.addHeader("Cookie", sessionId);
		Airport airport = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpClient.execute(createAirport);
			ObjectMapper om = new ObjectMapper();
			List<Airport> ListAirport = om.readValue(response.getEntity().getContent(),
					new TypeReference<List<Airport>>() {
					});
			airport = ListAirport.get(0);
			assertNotNull(airport);
			assertTrue(airport.getUuid().equals(user.getUuid()));
			assertEquals(201, response.getStatusLine().getStatusCode());

		}
		log.info("Created mock airport");

		log.info("Creating chat token");
		HttpPut landAirport = new HttpPut(baseUri + "/client/airport/landing");
		landAirport.addHeader("Cookie", sessionId);
		urlParameters = new ArrayList<BasicNameValuePair>();
		urlParameters.add(new BasicNameValuePair("airportUUID", airport.getUuid()));
		landAirport.setEntity(new UrlEncodedFormEntity(urlParameters));
		String token = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpClient.execute(landAirport);
			ObjectMapper om = new ObjectMapper();
			LandedAirport landed = om.readValue(response.getEntity().getContent(), LandedAirport.class);
			assertEquals(200, response.getStatusLine().getStatusCode());
			token = landed.user.get("chatToken");
			assertNotNull(token);
			assertTrue(landed.airport.getUuid().equals(airport.getUuid()));

		}
		log.info("Chat token created successfully");
		log.info("Test completed successfully!");
	}

	static class LandedAirport {
		public Map<String, String> user;
		public Airport airport;
	}
}
