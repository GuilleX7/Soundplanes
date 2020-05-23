package aiss.model.resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import aiss.listener.ObjectifyListener;
import aiss.model.geocoding.Location;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;

public class UserResource_JUnitTest {
	private static Closeable session;

	@BeforeClass
	public static void setup() {
		GoogleCredentials credentials = null;

		try {
			credentials = GoogleCredentials
					.fromStream(Thread.currentThread().getContextClassLoader()
							.getResourceAsStream(ObjectifyListener.GOOGLEIAM_AUTH_JSON))
					.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		DatastoreOptions options = DatastoreOptions.newBuilder().setProjectId(ObjectifyListener.PROJECT_ID)
				.setCredentials(credentials).build();
		Datastore datastore = options.getService();
		ObjectifyService.init(new ObjectifyFactory(datastore));
		ObjectifyService.register(User.class);
		ObjectifyService.register(Airport.class);
		ObjectifyService.register(AirportPlaylist.class);
	}

	@Before
	public void init() {
		session = ObjectifyService.begin();

		UserResource.removeAllUsers();
		UserResource.registerUser(User.of("uuid1", "User1", Location.ofCoordinates(0.0, 0.0)));
		UserResource.registerUser(User.of("uuid2", "User2", Location.ofCoordinates(1.0, 1.0)));
		UserResource.linkUserWithSpotifyId("uuid2", "spotifyId2");
		UserResource.registerUser(User.of("uuid3", "User3", Location.ofCoordinates(2.0, 2.0)));
		UserResource.linkUserWithFacebookId("uuid3", "facebookId3");
	}

	@After
	public void close() {
		session.close();
	}

	@Test
	public void getUsersTest() throws UnsupportedEncodingException {
		List<User> users = UserResource.getUsers();

		assertNotNull("Returned null", users);
		assertTrue("List doesn't contain 3 users", users.size() == 3);
	}

	@Test
	public void getUserTest() throws UnsupportedEncodingException {
		final String uuid = "uuid1";
		User user = UserResource.getUser(uuid);

		assertNotNull("User is null", user);
	}

	@Test
	public void registerUserTest() throws UnsupportedEncodingException {
		final String uuid = "uuid4";
		final String name = "User4";
		final Location location = Location.ofCoordinates(4.0, 4.0);
		final User newUser = User.of(uuid, name, location);

		UserResource.registerUser(newUser);

		User registeredUser = UserResource.getUser(uuid);
		assertNotNull("Register user is null", registeredUser);
		assertTrue("Registered name doesn't match", registeredUser.getName().contentEquals(name));
		assertTrue("Registered location doesn't match", registeredUser.getGeolocation().equals(location));
	}

	@Test
	public void linkUserWithFacebookIdTest() throws UnsupportedEncodingException {
		final String uuid = "uuid1";
		final String facebookId = "facebookId1";

		UserResource.linkUserWithFacebookId(uuid, facebookId);

		User linkedUser = UserResource.getUser(uuid);
		assertNotNull("Linked facebookId is null", linkedUser.getFacebookId());
		assertTrue("Linked facebookId doesn't match", linkedUser.getFacebookId().contentEquals(facebookId));
	}

	@Test
	public void linkUserWithSpotifyIdTest() throws UnsupportedEncodingException {
		final String uuid = "uuid1";
		final String spotifyId = "spotifyId1";

		UserResource.linkUserWithSpotifyId(uuid, spotifyId);

		User linkedUser = UserResource.getUser(uuid);
		assertNotNull("Linked spotifyId is null", linkedUser.getSpotifyId());
		assertTrue("Linked spotifyId doesn't match", linkedUser.getSpotifyId().contentEquals(spotifyId));
	}

	@Test
	public void getUserByFacebookIdTest() throws UnsupportedEncodingException {
		final String facebookId = "facebookId3";

		User user = UserResource.getUserByFacebookId(facebookId);

		assertNotNull("User is null", user);
		assertTrue("Linked facebookId doesn't match", user.getFacebookId().contentEquals(facebookId));
	}

	@Test
	public void getUserBySpotifyIdTest() throws UnsupportedEncodingException {
		final String spotifyId = "spotifyId2";

		User user = UserResource.getUserBySpotifyId(spotifyId);

		assertNotNull("User is null", user);
		assertTrue("Linked spotifyId doesn't match", user.getSpotifyId().contentEquals(spotifyId));
	}
}
