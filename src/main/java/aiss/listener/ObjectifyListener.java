package aiss.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import aiss.model.soundplanes.Airport;
import aiss.model.soundplanes.User;

/**
 * Application Lifecycle Listener implementation class ObjectifyListener
 *
 */
public class ObjectifyListener implements ServletContextListener {
	private final String GOOGLEIAM_AUTH_JSON = "WEB-INF/GoogleIAM_Auth.json";
	private final String PROJECT_ID = "soundplanes";

	private final Boolean PRODUCTION = false;
	private final String DATASTORE_EMULATOR_URL = "http://localhost:8081/";

	/**
	 * Default constructor.
	 */
	public ObjectifyListener() {
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		if (!PRODUCTION) {
			ObjectifyService.init(new ObjectifyFactory(DatastoreOptions.newBuilder().setHost(DATASTORE_EMULATOR_URL)
					.setProjectId(PROJECT_ID).build().getService()));
		} else {
			/*
			 * THE FOLLOWING CODE IS INTENDED FOR PRODUCTION USE ONLY. MAKE SURE THAT
			 * PRODUCTION IS SET TO FALSE WHEN RUNNING THE PROJECT LOCALLY. OTHERWISE THE
			 * PROJECT WILL USE PRODUCTION DATA.
			 */
			GoogleCredentials credentials = null;

			try {
				credentials = GoogleCredentials
						.fromStream(
								Thread.currentThread().getContextClassLoader().getResourceAsStream(GOOGLEIAM_AUTH_JSON))
						.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			DatastoreOptions options = DatastoreOptions.newBuilder().setProjectId(PROJECT_ID)
					.setCredentials(credentials).build();
			Datastore datastore = options.getService();
			ObjectifyService.init(new ObjectifyFactory(datastore));
		}

		ObjectifyService.register(User.class);
		ObjectifyService.register(Airport.class);
		System.out.println("ObjectifyService registered 2 classes");
	}
}
