package aiss.model.resource;

import aiss.model.spotify.Paging;
import aiss.model.spotify.Playlist;
import aiss.model.spotify.PlaylistTrack;
import aiss.model.spotify.Track;
import aiss.model.spotify.UserProfile;

import java.util.ArrayList;
import java.util.List;
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

    public Paging<Playlist> getPlaylists(Integer offset, Integer limit) {
        String url = API_URL + "/me/playlists";
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        Paging<Playlist> playlists = null;
        
        try {
            playlists = cr.get(Paging.PagingPlaylist.class);
        } catch (ResourceException re) {
            log.warning("Error when retrieving Spotify playlists: " + cr.getResponse().getStatus());
            log.warning(url);
        }
        
        return playlists;
    }
    
    public Paging<Playlist> getPlaylists(Integer offset) {
    	return getPlaylists(offset, 20);
    }
    
    public Paging<Playlist> getPlaylists() {
    	return getPlaylists(0, 20);
    }
    
    public Paging<PlaylistTrack> getPlaylistTracks(Playlist playlist, Integer offset, Integer limit) {
    	String url = String.format("%s/playlists/%s/tracks", API_URL, playlist.getId());
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        Paging<PlaylistTrack> tracks = null;
        
        try {
            tracks = cr.get(Paging.PagingPlaylistTrack.class);
        } catch (ResourceException re) {
            log.warning("Error when retrieving Spotify playlist tracks: " + cr.getResponse().getStatus());
            log.warning(url);
        }
        
        return tracks;
    }
    
    public Paging<PlaylistTrack> getPlaylistTracks(Playlist playlist, Integer offset) {
    	return getPlaylistTracks(playlist, offset, 100);
    }
    
    public Paging<PlaylistTrack> getPlaylistTracks(Playlist playlist) {
    	return getPlaylistTracks(playlist, 0, 100);
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
    
    public Playlist createEmptyPlaylist(UserProfile user, String name) {
    	String url = String.format("%s/users/%s/playlists", API_URL, user.getId());
        ClientResource cr = new ClientResource(url);

        ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);

        Playlist playlist = null;
        
        try {
            playlist = cr.post(String.format("name=%s", name), Playlist.class);
        } catch (ResourceException re) {
            log.warning("Error when creating a new Spotify playlist: " + cr.getResponse().getStatus());
            log.warning(url);
        }
        
        return playlist;
    }
    
    public Playlist createEmptyPlaylist(UserProfile user) {
    	return createEmptyPlaylist(user, user.getDisplayName() + "'s Soundplanes playlist");
    }
    
    public Boolean putTracksInPlaylist(Playlist playlist, List<Track> tracks) {
    	String url = String.format("%s/playlists/%s/tracks", API_URL, playlist.getId());
    	ClientResource cr = new ClientResource(url);
    	
    	ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        chr.setRawValue(accessToken);
        cr.setChallengeResponse(chr);
    	
        Boolean result = true;
        List<String> uris;
        
        try {
        	for (int offset = 0; offset < tracks.size() / 100; offset++) {
        		uris = new ArrayList<String>();
        		for (Track track : tracks.subList(offset, offset + 100)) {
        			uris.add(track.getUri());
        		}
        		cr.post(uris);
        	}
        } catch (ResourceException re) {
        	log.warning("Error when adding Spotify tracks to a playlist: " + cr.getResponse().getStatus());
            log.warning(url);
            result = false;
        }
        
		return result;
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
