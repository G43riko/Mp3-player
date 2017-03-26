package com.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class SpotifyData {
	private List<SpotifySong> songs = new ArrayList<SpotifySong>();
	private List<SpotifyPlaylist> playlists = new ArrayList<SpotifyPlaylist>();
	private Map<String, SpotifyArtist> artists = new HashMap<String, SpotifyArtist>();
	private int duplicateArtists = 0;
	//CLASES
	
	private class SpotifyPlaylist{
		private int total;
		private String href; 
		private List<SpotifySong> songs = new ArrayList<SpotifySong>();
		@Override
		public String toString() {
			return songs.size() + " / " + total + " = " + songs;
		}
	}
	
	public Object[][] getData(){
		Object[][] result = new Object[songs.size()][4];
		for(int i=0 ; i<songs.size() ; i++){
			SpotifySong song = songs.get(i);
			result[i][0] = song.id;
			result[i][1] = song.name;
			result[i][2] = song.duration;
			HashMap<String, String> songArtists = new HashMap<String, String>();
			for(SpotifyArtist artist : song.artists){
				songArtists.put(artist.id, artist.name);
			}
			result[i][3] = songArtists;
		}
		return result;
	};
	
	private class SpotifySong{
		private long duration;
		private String id;
		private String name;
		private String isrc;
		private String ean;
		private String preview;
		private String upc;
		private List<SpotifyArtist> artists = new ArrayList<SpotifyArtist>();
		@Override
		public String toString() {
			return name + "[" + id + "(" + isrc + ")](" + duration + ")";
		}
	}
	
	private class SpotifyArtist{
		private String id;
		private String name;
		private List<SpotifySong> songs = new ArrayList<SpotifySong>();
		@Override
		public String toString() {
			return name + "[" + id + "] - " + songs.size() + "songs";
		}
	}
	
	//ADDERS
	
	public SpotifyPlaylist addPlaylist(JSONObject object){
		SpotifyPlaylist playlist = createPlaylist(object);
		playlists.add(playlist);
		return playlist;
	}
	
	public SpotifySong addSong(JSONObject object){
		SpotifySong song = createSong(object);
		songs.add(song);
		return song;
	}
	
	//CREATORS
	
	private SpotifyPlaylist createPlaylist(JSONObject object){
		SpotifyPlaylist playlist = getPlaylist(object);
		JSONArray songs = object.getJSONArray("items");
		for(int i=0 ; i<songs.length() ; i++){
			JSONObject item = songs.getJSONObject(i);
			playlist.songs.add(createSong(item.getJSONObject("track")));
		}
		return playlist;
	}
	
	private SpotifySong createSong(JSONObject object){
		SpotifySong song = getSong(object);
		if(object.has("artists")){
			JSONArray artists = object.getJSONArray("artists");
			for(int i=0 ; i<artists.length() ; i++){
				SpotifyArtist artist = getArtist(artists.getJSONObject(i));
				artist.songs.add(song);
				song.artists.add(artist);
			}
		}
		return song;
	}
	
	//GETTERS
	
	public Set<String> getArtists(){
		return artists.values().stream().map(a -> a.name).collect(Collectors.toSet());
	}
	
	public Set<String> getSongs(){
		return songs.stream().map(a -> a.name).collect(Collectors.toSet());
	}
	
	private SpotifyPlaylist getPlaylist(JSONObject object){
		SpotifyPlaylist playlist = new SpotifyPlaylist();
		playlist.total = object.getInt("total");
		playlist.href = object.getString("href");
		return playlist;
	}
 	private SpotifyArtist getArtist(JSONObject object){
 		if(artists.containsKey(object.getString("id"))){
 			duplicateArtists++;
 			return artists.get(object.getString("id"));
 		}
 		
		SpotifyArtist artist = new SpotifyArtist();
		artist.id = object.getString("id");
		artist.name = object.getString("name");
		artists.put(artist.id, artist);
		return artist;
	}
 	
	private SpotifySong getSong(JSONObject object){
		SpotifySong song = new SpotifySong();
		if(object.has(" duration_ms")){
			song.duration = object.getLong(" duration_ms");
		}
		else if(object.has("duration_ms")){
			song.duration = object.getLong("duration_ms");
		}
		if(object.has("preview_url") && !object.isNull("preview_url")){
			song.preview = object.getString("preview_url");
		}
		if(object.has("external_ids")){
			JSONObject urls = object.getJSONObject("external_ids");
			if(urls.has("isrc")){
				song.isrc = urls.getString("isrc");
			}

			if(urls.has("ean")){
				song.ean = urls.getString("ean");
			}

			if(urls.has("upc")){
				song.upc = urls.getString("upc");
			}
		}
		if(object.has("name")){
			song.name = object.getString("name");
		}
		if(object.has("id")){
			song.id = object.getString("id");
		}
		return song;
	}
	
	//OTHERS


	public String toString(){
		return "(" + songs.size() + "): " + songs;
//		return "(" + playlists.size() + "): " + playlists;
	}

	public void show() {
		System.out.println("Songs: " + songs.size());
		System.out.println("Artists: " + artists.size());
		System.out.println("duplicitných artistov: " + duplicateArtists);
		artists.values().stream()
						.sorted((a, b) -> b.songs.size() - a.songs.size())
						.map(a -> a.name + ": " + a.songs.size())
						.limit(10)
						.forEach(System.out::println);
	}
}
