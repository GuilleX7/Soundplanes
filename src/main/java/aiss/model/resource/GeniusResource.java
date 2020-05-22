package aiss.model.resource;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import aiss.model.genius.Hit;
import aiss.model.genius.Meta;
import aiss.model.genius.SearchRequest;
import aiss.model.genius.Song;

public class GeniusResource {
	private static Logger log = Logger.getLogger(GeniusResource.class.getName());

	private static final String API_URL = "https://api.genius.com";
	private static final String API_KEY = "DWMo06PyLfXWFF29D7Rb2M7wsyMiu2TGKIRe63LJnhfVHeYRibGuePH5ramU_W84";

	public static List<Song> searchSong(String title) throws ResourceException {
		String searchURL = String.format("%s/search", API_URL);
		ClientResource cr = new ClientResource(searchURL);
		cr.addQueryParameter("q", title);
		
		ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
		chr.setRawValue(API_KEY);
		cr.setChallengeResponse(chr);
		
		SearchRequest request = null;
		
		List<Song> songs = new ArrayList<Song>();

		try {
			request = cr.get(SearchRequest.class);
		} catch (ResourceException re) {
			log.warning(re.getMessage());
			return null;
		}

		Meta meta = request.getMeta();
		if (meta.getStatus() != 200) {
			log.warning(String.format("Genius API returned error code %d: %s", meta.getStatus(),
					(meta.getMessage() == null) ? "" : meta.getMessage()));
			return null;
		}
		
		List<Hit> hits = request.getResponse().getHits();
		for (Hit hit : hits) {
			if (hit.getType().contentEquals("song")) {
				songs.add(hit.getSong());
			}
		}
		
		return songs;
	}

	public static String getLyricsFromSong(Song song) throws IOException {
		String lyrics = null;
		Document doc = null;

		try {
			doc = Jsoup.connect(song.getUrl()).get();
		} catch (IOException e) {
			log.warning(song.getUrl() + " couldn't be fetched");
			return lyrics;
		}
		
		Elements sections = doc.body().getElementsByClass("lyrics");
		if (sections.size() > 0) {
			lyrics = sections.get(0).wholeText().trim();
		} else {
			Element section = doc.body().getElementById("lyrics");
			if (section != null) {
				while (section.nextElementSibling() != null) {
					section = section.nextElementSibling();
					lyrics += section.html();
				}
			} else {
				log.warning("Couldn't fetch lyrics for " + song.getTitle() + " at " + song.getUrl());
			}
		}
		
		return lyrics;
	}
}
