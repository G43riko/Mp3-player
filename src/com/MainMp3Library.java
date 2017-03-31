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
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
public class MainMp3Library{
//	static AdvancedPlayer player = null;;
	private final static String FILE_LOCAL_SONGS = "data2.txt";
	private final static String FILE_SPOTIFY_SONGS	= "spotifySongs2";
	static File playing = null;
	Library lib = new Library();
	PlayerManager playerManager = new PlayerManager();
	GuiManager guiManager;
	
	
	public MainMp3Library(String path){

//		SpotifyAPI.getAllPlaylistSongsToFile("11172655892", "3YuD9dHloyFTYcEbAiAViY", "spotifySongs2");	
//		System.exit(1);
		lib.importFileFromFile(FILE_LOCAL_SONGS);
//		lib.importFilesFromDirectory(path);
		
		
		System.out.println(lib.showStats());
		SpotifyAPI.loadSongsFromFile(FILE_SPOTIFY_SONGS);
		
		/*
		
		Object[][] data = SpotifyAPI.getData().getData();
		for(int i=0 ; i<data.length ; i++){
			Song song = lib.addSong((String)data[i][0], (String)data[i][1], (long)data[i][2]);
			HashMap<String, String> songArtists = (HashMap<String, String>)data[i][3];
			for(Entry<String, String> artistData : songArtists.entrySet()){
				Artist artist = lib.addArtist(artistData.getKey(), artistData.getValue());
//				System.out.println(artist);
				song.addArtist(artist);
				artist.addSong(song);
			}
		}
		System.out.println(lib.showStats());
		
		int counter = 0, sum = 0, two = 0;
		for(Artist a : lib.getArtistsList()){
			if(!a.hasSpotifyData()){
				continue;
			}
			Set<String> artists = lib.getSimilarArtists(a.getBestTitle().toLowerCase());
			if(artists.size() > 1){
				if(artists.size() == 2){
					artists.stream().forEach(aa -> {
						Artist artist = lib.getArtist(aa);
						if(artist == null || a == artist){
							return;
						}
						String title = artist.getBestTitle().replaceAll(a.getBestTitle(), "").replaceAll(a.getBestTitle().toLowerCase(), "");
						Artist.subTitle(artist, title);
						lib.divideArtist(artist);
						//System.out.println("vysledok pre " + a.getBestTitle() + " je: " +  title);
					});
				}
				//System.out.println(a.getBestTitle());
			}
		}
		//counter: 424 = 10307 = 259
		//counter: 117 = 311 = 80
		
//		lib.divideArtists();
		System.out.println(lib.showStats());
		System.out.println("counter: " + counter + " = " + sum + " = " + two);
//		System.exit(1);
		*/

//		Set<String> artists = SpotifyAPI.getData().getArtists();
//		long know = artists.stream().filter(a -> lib.getArtist(a) != null).peek(System.out::println).count();
//		System.out.println("pozna : " + know  + " z " + (artists.size()));
//		
//		
//		System.exit(0);
//		

//		WebV
		guiManager = new GuiManager(lib, playerManager);
		
		FileManager.saveToFile("data2.txt", lib.toJson().toString());
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
