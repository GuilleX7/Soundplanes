package aiss.model.resource;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import aiss.listener.ObjectifyListener;
import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.AirportPlaylist;
import aiss.model.soundplanes.User;

public class AirportResource_JUnitTest {
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
	}

	@After
	public void close() {
		session.close();
	}
}
