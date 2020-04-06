package aiss.model.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CountryStatesResource {

	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());

	protected static final String STATES_RESOURCE = "WEB-INF/states.json";

	static Map<String, List<String>> states;

	@SuppressWarnings("unchecked")
	private static void loadResources() {
		states = new HashMap<>();
		ObjectMapper om = new ObjectMapper();
		try {

			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(STATES_RESOURCE);

			if (is == null)
				log.warning("Unable read file " + STATES_RESOURCE);
			else {
				states = (Map<String, List<String>>) om.readValue(is,
						new TypeReference<HashMap<String, List<String>>>() {
						});
			}
			log.info(states.size() + " states configurations Loaded!");
		} catch (IOException e) {
			log.log(Level.WARNING, "Unable to load states configuration from " + STATES_RESOURCE);
			log.log(Level.WARNING, e.getMessage());
		}
	}

	public static Map<String, List<String>> getStates() {
		if (states == null) {
			loadResources();
		}
		return states;
	}
}
