package aiss.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import aiss.model.geocoding.Location;
import aiss.model.soundplanes.User;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class UserFlow_IntegrationTest {
	private static final Logger log = Logger.getLogger(UserFlow_IntegrationTest.class.getName());
	final String baseUri = "http://localhost:8090";

	@Test
	public void UserFlowTest() throws IOException {
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

		HttpPut createAirport = new HttpPut(baseUri + "/client/airports");
		createAirport.addHeader("Cookie", sessionId);
		log.info("Creating airport without logging with Spotify");
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpClient.execute(createAirport);
			assertEquals(403, response.getStatusLine().getStatusCode());
		}
		log.info("Test completed succesfully!");
	}
}
