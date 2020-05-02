package aiss.model.resource;

import java.util.logging.Logger;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import aiss.model.facebook.UserProfile;

public class FacebookResource {
	private static final Logger log = Logger.getLogger(SpotifyResource.class.getName());

    private final String ACCESS_TOKEN;
    private final static String API_URL = "https://graph.facebook.com";

    public FacebookResource(String access_token) {
        this.ACCESS_TOKEN = access_token;
    }

    public UserProfile getUserProfile() {
        String url = API_URL + "/me/";
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(ACCESS_TOKEN);
        cr.setChallengeResponse(chr);

        UserProfile profile = null;
        try {
            profile = cr.get(UserProfile.class);
        } catch (ResourceException re) {
            log.warning("Error when retrieving Facebook profile: " + cr.getResponse().getStatus());
            log.warning(url);
        }
        
        return profile;
    }
}
