package aiss.model.resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.bp.Instant;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import aiss.model.geocoding.Location;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;
import aiss.model.spotify.Track;

public class AirportResource_JUnitTest {
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
	public void init() throws IOException {
		session = ObjectifyService.begin();
		localDatastoreHelper.reset();

		UserResource.registerUser(User.of("uuid1", "user1", Location.ofCoordinates(0.0, 0.0)));
		AirportResource.removeAllAirports();
		AirportResource.removeAllAirportPlaylists();
		AirportResource.registerAirport(Airport.of("uuid1"));
		AirportResource.registerAirportPlaylist(AirportPlaylist.empty("uuid1"));
	}

	@After
	public void end() throws IOException {
		session.close();
		localDatastoreHelper.reset();
	}

	@AfterClass
	public static void close() throws IOException, InterruptedException, TimeoutException {
		localDatastoreHelper.stop();
	}
	
	@Test
	public void getAirportTest() {
		final String uuid = "uuid1";
		Airport airport = AirportResource.getAirport(uuid);

		assertNotNull("Airport is null", airport);
		assertTrue("Registered uuid doesn't match", airport.getUuid().contentEquals(uuid));
	}
	
	@Test
	public void getAllAirportsTest() {
		List<Airport> airports = AirportResource.getAllAirports();
		
		assertNotNull("Airports is null", airports);
		assertTrue("There are no airports", airports.size() > 0);
	}
	
	@Test
	public void getAirportsAfter() {
		final Long timestamp = Instant.now().getEpochSecond();
		final Long everTimestamp = 0L;
		
		List<Airport> airports = AirportResource.getAirportsAfter(timestamp);
		
		assertNotNull("Airports is null", airports);
		assertTrue("There are airports", airports.size() == 0);
		
		airports = AirportResource.getAirportsAfter(everTimestamp);
		
		assertNotNull("Airports is null", airports);
		assertTrue("There aren't airports", airports.size() > 0);
	}
	
	@Test
	public void registerAirportTest() {
		final String uuid = "uuid1";
		AirportResource.registerAirport(Airport.of(uuid));
		Airport airport = AirportResource.getAirport(uuid);
		
		assertNotNull("Airport is null", airport);
		assertTrue("Registered uuid doesn't match", airport.getUuid().contentEquals(uuid));
	}
	
	@Test
	public void removeAllAirportsTest() {
		AirportResource.removeAllAirports();
		
		List<Airport> airports = AirportResource.getAllAirports();
		
		assertNotNull("Airports is null", airports);
		assertTrue("There are airports", airports.size() == 0);
	}
	
	@Test
	public void getAirportPlaylistTest() {
		final String uuid = "uuid1";
		AirportPlaylist airportPlaylist = AirportResource.getAirportPlaylist(uuid);
		
		assertNotNull("AirportPlaylist is null", airportPlaylist);
		assertTrue("Registered uuid doesn't match", airportPlaylist.getUuid().contentEquals(uuid));
	}
	
	@Test
	public void registerAirportPlaylistTest() {
		final String uuid = "uuid1";
		final List<Track> tracks = new ArrayList<Track>();
		tracks.add(Track.of(null, null, null, null));
		AirportResource.registerAirportPlaylist(AirportPlaylist.of(uuid, tracks));
		
		AirportPlaylist airportPlaylist = AirportResource.getAirportPlaylist(uuid);
		
		assertNotNull("AirportPlaylist is null", airportPlaylist);
		assertTrue("AirportPlaylist doesn't contain tracks", airportPlaylist.getTracks().size() > 0);
	}
}
