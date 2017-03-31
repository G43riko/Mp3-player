package com.API.spotify;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.library.Artist;
import com.utils.FileManager;
import com.utils.HttpHandler;

public class SpotifyAPI {
	static SpotifyData data = new SpotifyData();
	private final static String AUTH = "BQCxRJsjiLK8AKmKQ5elTD4x5Po00tzoLjd3hnXSzrVTc48XfEeh-O-w2aImMX1neEh9lZNA_dc2FQGlVbQ0IlX6-Gdf1tzpOlV14mzu_QQIUEKAIuwyt5lb5t5mqobAYjhL_5yktsvOhVhSpHn-ldt5mfQ_";
	
	private final static String SONG_URL = "https://api.spotify.com/v1/tracks/";
	private final static String SEARCH_URL = "https://api.spotify.com/v1/search";
	private final static int SEARCH_LIMIT = 50;
	
	public static SpotifyData getData(){
		return data;
	}
	
	public final static JSONObject loadSongsFromFile(String file){
		JSONObject result = new JSONObject(FileManager.loadFromFile(new File(file)));
		
		JSONArray songs = result.getJSONArray("songs");
		for(int i=0 ; i<songs.length() ; i++){
			data.addSong(songs.getJSONObject(i).getJSONObject("track"));
		}
//		System.out.println(data);
//		data.show();
		return result;
	}
	
	public final static JSONObject getAllPlaylistSongsToFile(String user, String playlist, String file){
		Map<String, String> header = new HashMap<String, String>();
		header.put("Accept", "application/json");
		header.put("Authorization", "Bearer " + AUTH);
		

		String url = "https://api.spotify.com/v1/users/" + user + "/playlists/" + playlist + "/tracks";
		
		JSONObject result = new JSONObject();

		JSONArray allSongs = new JSONArray();
		try{
			do{
				String response = HttpHandler.getResponse(url, header);
				JSONObject object = new JSONObject(response);
				JSONArray songs = object.getJSONArray("items");
				for(int i=0 ; i<songs.length() ; i++){
					allSongs.put(songs.getJSONObject(i));
				}
				System.out.println("načítalo sa a teraz počet zaznamov: " + allSongs.length());
				Thread.sleep(1000);
				
				if(object.isNull("next"))
					break;
				url = object.getString("next");
			}while(true);
			
			result.put("songs", allSongs);
			FileManager.saveToFile(new File(file), result.toString());
		}
		catch(Exception e){
			System.out.println("chyba pri vytváraní kompletneho zoznamu: " + e);
		}
		return result;
	}
	
	public final static JSONObject search(String key){
		String type = "artist,track";
		String url = SEARCH_URL + "?q=" + key.replace(" ", "+") + "&type=" + type + "&limit=" + SEARCH_LIMIT; 
		Map<String, String> header = new HashMap<String, String>();
		header.put("Accept", "application/json");
		header.put("Authorization", "Bearer " + AUTH);
		
		JSONObject result;
		try{
			String response = HttpHandler.getResponse(url, header);
			System.out.println("response: " + response);
			result = new JSONObject(response);
		}
		catch(Exception e){
			System.out.println("padlo vyhladavanie na: " + e);
			result = new JSONObject();
		}
		return result;
	}
	
	public final static JSONObject getPlaylistJSON(String user, String playlist){
		Map<String, String> header = new HashMap<String, String>();
		header.put("Accept", "application/json");
		header.put("Authorization", "Bearer " + AUTH);

		JSONObject result;
		String url = "https://api.spotify.com/v1/users/" + user + "/playlists/" + playlist + "/tracks";
		try{
			String response = HttpHandler.getResponse(url, header);
			System.out.println("response: " + response);
			result = new JSONObject(response);
		}
		catch(Exception e){
			System.out.println("padlo načítavanie playlistu na: " + e);
			result = new JSONObject();
		}
		data.addPlaylist(result);
		return result;
	}
	
	public final static JSONObject getSongJSON(String name){
		if(name.isEmpty()){
			name = "5MetyLgSG8s1JUbKRpNaQM"; 
		}
		JSONObject result;
		try{
			result = new JSONObject(HttpHandler.getResponse(SONG_URL + name));
		}
		catch(Exception e){
			System.out.println("padlo načítavanie pesničky na: " + e);
			result = new JSONObject();
		}
		data.addSong(result);
		
		
		return result;
	}
}
