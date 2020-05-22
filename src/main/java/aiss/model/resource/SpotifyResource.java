package aiss.model.resource;

import aiss.model.spotify.Paging;
import aiss.model.spotify.Playlist;
import aiss.model.spotify.PlaylistTrack;
import aiss.model.spotify.Track;
import aiss.model.spotify.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class SpotifyResource {
    private static final Logger log = Logger.getLogger(SpotifyResource.class.getName());

    private final String accessToken;
    private final static String API_URL = "https://api.spotify.com/v1";

    public static SpotifyResource fromToken(String accessToken) {
    	return new SpotifyResource(accessToken);
    }
    
    private SpotifyResource(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<Playlist> getPlaylists() {
        String url = API_URL + "/me/playlists";
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        List<Playlist> playlists = new ArrayList<Playlist>();
        Paging<Playlist> page = null;
        
        cr.addQueryParameter("offset", "0");
        cr.addQueryParameter("limit", "50");
        try {
            page = cr.get(Paging.PagingPlaylist.class);
        } catch (ResourceException re) {
            log.warning("Error when retrieving Spotify playlists: " + cr.getResponse().getStatus());
            log.warning(url);
            return null;
        }
        
        playlists.addAll(page.getItems());
        for (int offset = page.getOffset() + page.getLimit(); offset < page.getTotal(); offset += page.getLimit()) {
        	cr = new ClientResource(url);
            cr.setChallengeResponse(chr);
        	
        	cr.addQueryParameter("offset", String.valueOf(offset));
        	cr.addQueryParameter("limit", String.valueOf(page.getLimit()));
        	try {
                page = cr.get(Paging.PagingPlaylist.class);
            } catch (ResourceException re) {
                log.warning("Error when retrieving Spotify playlists: " + cr.getResponse().getStatus());
                log.warning(url);
                return null;
            }
        	
        	playlists.addAll(page.getItems());
        }
        
        return playlists;
    }
    
    public List<PlaylistTrack> getPlaylistTracks(Playlist playlist) {
    	String url = String.format("%s/playlists/%s/tracks", API_URL, playlist.getId());
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        List<PlaylistTrack> tracks = new ArrayList<PlaylistTrack>();
        Paging<PlaylistTrack> page = null;
        
        cr.addQueryParameter("offset", "0");
        cr.addQueryParameter("limit", "100");
        try {
            page = cr.get(Paging.PagingPlaylistTrack.class);
        } catch (ResourceException re) {
            log.warning("Error when retrieving Spotify tracks: " + cr.getResponse().getStatus());
            log.warning(url);
            return null;
        }
        
        System.out.println(page);
        tracks.addAll(page.getItems());
        for (int offset = page.getOffset() + page.getLimit(); offset < page.getTotal(); offset += page.getLimit()) {
        	cr = new ClientResource(url);
            cr.setChallengeResponse(chr);
            
        	cr.addQueryParameter("offset", String.valueOf(offset));
        	cr.addQueryParameter("limit", String.valueOf(page.getLimit()));
        	try {
                page = cr.get(Paging.PagingPlaylistTrack.class);
            } catch (ResourceException re) {
                log.warning("Error when retrieving Spotify tracks: " + cr.getResponse().getStatus());
                log.warning(url);
                return null;
            }
        	System.out.println(page);
        	tracks.addAll(page.getItems());
        }
        
        return tracks;
    }
    
    public Playlist getPlaylist(String playlistId) {
    	String url = String.format("%s/playlists/%s", API_URL, playlistId);
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        Playlist playlist = null;
        
        try {
            playlist = cr.get(Playlist.class);
        } catch (ResourceException re) {
            log.warning("Error when retrieving Spotify playlist: " + cr.getResponse().getStatus());
            log.warning(url);
        }
        
        return playlist;
    }
    
    public Playlist createEmptyPlaylist(String userId, String name, Boolean isPublic, String description) {
    	String url = String.format("%s/users/%s/playlists", API_URL, userId);
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        Playlist playlist = null;
        
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("name", name);
        query.put("public", isPublic);
        query.put("description", description);
        
        try {
            playlist = cr.post(query, Playlist.class);
        } catch (ResourceException re) {
            log.warning("Error when creating a new Spotify playlist: " + cr.getResponse().getStatus());
            log.warning(url);
        }
        
        return playlist;
    }
    
    public Boolean putTracksInPlaylist(Playlist playlist, List<Track> tracks) throws IOException {
    	String url = String.format("%s/playlists/%s/tracks", API_URL, playlist.getId());
    	ClientResource cr = new ClientResource(url);
    	
    	ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);
    	
        Boolean success = true;
        List<String> uris;
        
    	for (int offset = 0, limit; offset < tracks.size(); offset += 100) {
    		uris = new ArrayList<String>();
    		limit = Math.min(offset + 100, tracks.size());
    		for (int i = offset; i < limit; i++) {
    			uris.add(tracks.get(i).getUri());
    		}
    		
    		try {
    			cr.post(uris);
            } catch (ResourceException re) {
            	log.warning("Error when adding Spotify tracks to a playlist: " + cr.getResponse().getStatus());
                log.warning(url);
                success = false;
                break;
            }
    	}
        
		return success;
    }

    public UserProfile getUserProfile() {
        String url = API_URL + "/me";
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        log.info("Retrieving user Spotify profile");

        UserProfile profile = null;
        try {
            profile = cr.get(UserProfile.class);
        } catch (ResourceException re) {
            log.warning("Error when retrieving the user Spotify profile: " + cr.getResponse().getStatus());
            log.warning(url);
        }
        
        return profile;
    }
}
