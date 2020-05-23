package aiss.model.resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import aiss.model.geocoding.Location;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;

public class UserResource_JUnitTest {
	private static Closeable session;
	protected static LocalDatastoreHelper localDatastoreHelper;
	private static String localDatastoreHelperHost;

	@BeforeClass
	public static void setup() throws IOException, InterruptedException {
        System.out.println("[Datastore-Emulator] start");
        localDatastoreHelper = LocalDatastoreHelper.create();
        localDatastoreHelper.start();
        System.out.println("[Datastore-Emulator] listening on port: " + localDatastoreHelper.getPort());
        localDatastoreHelperHost = String.format("http://localhost:%d", localDatastoreHelper.getPort());
        
        ObjectifyService.init(new ObjectifyFactory(DatastoreOptions.newBuilder().setHost(localDatastoreHelperHost)
				.build().getService()));
		ObjectifyService.register(User.class);
		ObjectifyService.register(Airport.class);
		ObjectifyService.register(AirportPlaylist.class);
	}

	@Before
	public void init() {
		session = ObjectifyService.begin();

		UserResource.removeAllUsers();
		UserResource.registerUser(User.of("uuid1", "User1", Location.ofCoordinates(1.0, 1.0)));
		UserResource.linkUserWithFacebookId("uuid1", "facebookId1");
		UserResource.linkUserWithSpotifyId("uuid1", "spotifyId1");
	}

	@After
	public void end() throws IOException {
		session.close();
	}

	@AfterClass
	public static void close() throws IOException, InterruptedException, TimeoutException {
		localDatastoreHelper.stop();
	}

	@Test
	public void getUsersTest() throws UnsupportedEncodingException {
		List<User> users = UserResource.getUsers();

		assertNotNull("Returned null", users);
		assertTrue("List doesn't contain users", users.size() > 0);
	}

	@Test
	public void getUserTest() throws UnsupportedEncodingException {
		final String uuid = "uuid1";
		User user = UserResource.getUser(uuid);

		assertNotNull("User is null", user);
	}

	@Test
	public void registerUserTest() throws UnsupportedEncodingException {
		final String uuid = "uuid2";
		final String name = "User2";
		final Location location = Location.ofCoordinates(2.0, 2.0);
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
		final String facebookId = "facebookId2";

		UserResource.linkUserWithFacebookId(uuid, facebookId);

		User linkedUser = UserResource.getUser(uuid);
		assertNotNull("Linked facebookId is null", linkedUser.getFacebookId());
		assertTrue("Linked facebookId doesn't match", linkedUser.getFacebookId().contentEquals(facebookId));
	}

	@Test
	public void linkUserWithSpotifyIdTest() throws UnsupportedEncodingException {
		final String uuid = "uuid1";
		final String spotifyId = "spotifyId2";

		UserResource.linkUserWithSpotifyId(uuid, spotifyId);

		User linkedUser = UserResource.getUser(uuid);
		assertNotNull("Linked spotifyId is null", linkedUser.getSpotifyId());
		assertTrue("Linked spotifyId doesn't match", linkedUser.getSpotifyId().contentEquals(spotifyId));
	}

	@Test
	public void getUserByFacebookIdTest() throws UnsupportedEncodingException {
		final String facebookId = "facebookId1";

		User user = UserResource.getUserByFacebookId(facebookId);

		assertNotNull("User is null", user);
		assertTrue("Linked facebookId doesn't match", user.getFacebookId().contentEquals(facebookId));
	}

	@Test
	public void getUserBySpotifyIdTest() throws UnsupportedEncodingException {
		final String spotifyId = "spotifyId1";

		User user = UserResource.getUserBySpotifyId(spotifyId);

		assertNotNull("User is null", user);
		assertTrue("Linked spotifyId doesn't match", user.getSpotifyId().contentEquals(spotifyId));
	}

	@Test
	public void removeAllUsersTest() {
		UserResource.removeAllUsers();

		List<User> users = UserResource.getUsers();

		assertNotNull("Users is null", users);
		assertTrue("There are users", users.size() == 0);
	}
}
