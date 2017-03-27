package com;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.json.JSONObject;

import com.API.spotify.SpotifyAPI;
import com.API.spotify.SpotifyData;
import com.gui.ArtistsViewer;
import com.gui.BottomMenu;
import com.gui.GuiManager;
import com.gui.MenuPanel;
import com.gui.SongsViewer;
import com.library.Artist;
import com.library.Library;
import com.library.Song;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.PlayerManager;
import com.utils.FileManager;
import com.utils.FileSearcher;
import com.utils.HttpHandler;
import com.utils.Matcher;

import javazoom.jl.player.advanced.AdvancedPlayer;

public class MainMp3Library {
//	static AdvancedPlayer player = null;;
	private final static String FILE_LOCAL_SONGS = "data.txt";
	private final static String FILE_SPOTIFY_SONGS	= "spotifySongs";
	static File playing = null;
	Library lib = new Library();
	PlayerManager playerManager = new PlayerManager();
	GuiManager guiManager;
	
	
	
	public MainMp3Library(String path){
		lib.importFileFromFile(FILE_LOCAL_SONGS);
//		lib.importFilesFromDirectory(path);
		
		
		System.out.println(lib.showStats());
		SpotifyAPI.loadSongsFromFile(FILE_SPOTIFY_SONGS);
		
//		Matcher.JoinSpotifySongs(SpotifyAPI.getData(), lib);
//		System.exit(1);
		
		
//		Object[][] data = SpotifyAPI.getData().getData();
//		for(int i=0 ; i<data.length ; i++){
//			Song song = lib.addSong((String)data[i][0], (String)data[i][1], (long)data[i][2]);
//			HashMap<String, String> songArtists = (HashMap<String, String>)data[i][3];
//			for(Entry<String, String> artistData : songArtists.entrySet()){
//				Artist artist = lib.addArtist(artistData.getKey(), artistData.getValue());
////				System.out.println(artist);
//				song.addArtist(artist);
//				artist.addSong(song);
//			}
//		}
//		System.out.println(lib.showStats());
		
		

//		Set<String> artists = SpotifyAPI.getData().getArtists();
//		long know = artists.stream().filter(a -> lib.getArtist(a) != null).peek(System.out::println).count();
//		System.out.println("pozna : " + know  + " z " + (artists.size()));
//		
//		
//		System.exit(0);
//		

//		WebV
		guiManager = new GuiManager(lib, playerManager);
		
		
		
		
//		FileManager.saveToFile("data.txt", lib.toJson().toString());
	}
	
	
	private static int LevenshteinDistance(String s, String t){
	    // degenerate cases
	    if (s == t) return 0;
	    if (s.length() == 0) return t.length();
	    if (t.length() == 0) return s.length();

	    // create two work vectors of integer distances
	    int[] v0 = new int[t.length() + 1];
	    int[] v1 = new int[t.length() + 1];

	    // initialize v0 (the previous row of distances)
	    // this row is A[0][i]: edit distance for an empty s
	    // the distance is just the number of characters to delete from t
	    for (int i = 0; i < v0.length; i++)
	        v0[i] = i;

	    for (int i = 0; i < s.length(); i++)
	    {
	        // calculate v1 (current row distances) from the previous row v0

	        // first element of v1 is A[i+1][0]
	        //   edit distance is delete (i+1) chars from s to match empty t
	        v1[0] = i + 1;

	        // use formula to fill in the rest of the row
	        for (int j = 0; j < t.length(); j++)
	        {
	            int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
	            v1[j + 1] = Math.max(v1[j] + 1, Math.max(v0[j + 1] + 1, v0[j] + cost));
	        }

	        // copy v1 (current row) to v0 (previous row) for next iteration
	        for (int j = 0; j < v0.length; j++)
	            v0[j] = v1[j];
	    }

	    return v1[t.length()];
	}
	
	

	
	public static void main(String[] args) {
//		SpotifyAPI.loadSongsFromFile("spotifySongs");
//		SpotifyAPI.getSongJSON("");
//		SpotifyAPI.getAllPlaylistSongsToFile("11172655892", "3YuD9dHloyFTYcEbAiAViY", "spotifySongs");	
//		SpotifyAPI.getPlaylistJSON("11172655892", "4cUVp8wE7lmPFnZxuT3FS2");
//		System.out.println(SpotifyAPI.search("vicetone overtime remix"));
		
		new MainMp3Library("/home/gabriel/Hudba");
	}

}
