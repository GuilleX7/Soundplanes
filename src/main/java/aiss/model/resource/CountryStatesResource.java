package aiss.model.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CountryStatesResource {
	private static Logger log = Logger.getLogger(CountryStatesResource.class.getName());

	private static Map<String, List<String>> states;
	
	private static final String COUNTRIES_FOLDER = "WEB-INF/states/";

	private static String getCountryFileUri(String country) {
		return String.format("%s%s.json", COUNTRIES_FOLDER, country);
	}

	private static void loadStates(String country) {
		if (states == null) {
			states = new HashMap<String, List<String>>();
		}

		if (states.containsKey(country)) {
			return;
		}
		
		ObjectMapper om = new ObjectMapper();
		final String countryFileUri = getCountryFileUri(country);
		
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(countryFileUri);

			if (is == null) {
				log.warning("Unable read file " + countryFileUri);
			} else {
				states.put(country, Arrays.asList(om.readValue(is, String[].class)));
			}
			log.info(states.get(country).size() + " states for country " + country + " Loaded!");
		} catch (IOException e) {
			log.log(Level.WARNING, "Unable to load states from " + countryFileUri);
			log.log(Level.WARNING, e.getMessage());
		}
	}

	public static List<String> getStates(String country) {
		loadStates(country);
		return states.getOrDefault(country, new ArrayList<String>());
	}
}
