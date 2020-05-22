package aiss.model.resource;

import java.util.logging.Logger;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import aiss.model.facebook.UserProfile;

public class FacebookResource {
	private static final Logger log = Logger.getLogger(SpotifyResource.class.getName());

    private final String accessToken;
    private final static String API_URL = "https://graph.facebook.com";

    public static FacebookResource fromToken(String accessToken) {
    	return new FacebookResource(accessToken);
    }
    
    private FacebookResource(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserProfile getUserProfile() {
        String url = API_URL + "/me/";
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
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
