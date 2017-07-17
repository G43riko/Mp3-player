package com.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gui.Pos;

public class ArtistManager {
	protected HashMap<String, Artist> artists = new HashMap<String, Artist>();
	protected List<Artist> artistsPossibleJoined = new LinkedList<Artist>();
	

	public Artist addArtist(String title){
		title = title.toLowerCase();
		if(artists.containsKey(title)){
			return artists.get(title);
		}
		Artist artist = new Artist(title);
		if(title.toLowerCase().contains("feat") || 
		   title.contains(";") || 
		   title.contains(",") || 
		   title.contains("&") || 
		   title.toLowerCase().contains(" vs ") || 
		   title.toLowerCase().contains(" ft") ||
		   title.toLowerCase().contains("feat.") ||
		   title.toLowerCase().contains(" and ")){
			artistsPossibleJoined.add(artist);
		}
		artists.put(title, artist);
		return artist;
	}
	
	public Artist addArtist(JSONObject object){
		Artist artist = null;
		if(object.has("spotifyId")){
			artist = artists.get(object.getString("spotifyId"));
			if(artist != null){
				return artist;
			}
		}
		if(object.has("title")){
			artist = artists.get(object.getString("title"));
			if(artist != null){
				return artist;
			}
		}
		
		artist = new Artist(object);
		if(artist.hasSpotifyData()){
			artists.put(artist.getSpotifyId(), artist);
		}
		else{
			artists.put(artist.getBestTitle(), artist);
		}
		return artist;
	}
	
	public Artist addArtist(String id, String name){
		Artist artist = null;
		
		//ak existuje podla nazvu
		if(artists.containsKey(name.toLowerCase())){
			artist = artists.get(name.toLowerCase());
			artist.setSpotifyData(id, name);
			return artist;
		}
		
		//ak existuje podla id
		if(artists.containsKey(id)){
			artist = artists.get(id);
			artist.setSpotifyData(id, name);
			return artist;
		}
		artist = new Artist(id, name);
		artists.put(name.toLowerCase(), artist);
		return artist;
	}
	
	@Override
	public String toString() {
		return String.join("\n", artists.values().stream()
												 .map(a -> a.toString() + "(" + a.getNumberOfSongs() + ")")
												 .collect(Collectors.toList()));
	}
	
	public Collection<Artist> getValues(){
		return artists.values();
	}
	
	public void put(String key, Artist value){
		artists.put(key, value);
	}
	public void remove(String key){
		artists.remove(key);
	}
	
	public void search(String key){
		String finalKey = Library.TransformToBasicForm(key);
		artists.values().stream()
						.filter(a -> Library.TransformToBasicForm(a.getBestTitle()).contains(finalKey))
						.collect(Collectors.toList());
	}
	
	public List<String> getArtistsWithNoSong(){
		return artists.values().stream().filter(a -> a.getNumberOfSongs() == 0)
							   .map(a -> a.getBestTitle())
							   .collect(Collectors.toList());
	}

	public Object[][] getData(){
		Object[][] result = new Object[artists.size()][Pos.ArtistsTitlesSize()];
		int i=0;
		for(Artist a : artists.values()){
			result[i][Pos.ARTISTS_ID.getId()] = i + 1;
			result[i][Pos.ARTISTS_NAME.getId()] = a.getBestTitle();
			result[i][Pos.ARTISTS_SONGS.getId()] = a.getNumberOfSongs();
			result[i][Pos.ARTISTS_INFO.getId()] = a;
//				result[i][Pos.ARTISTS_SPOTIFY.getId()] = a.hasSpotifyData() ? "yes" : "no";
			result[i][Pos.ARTISTS_SPOTIFY.getId()] = a.songsWithSpotify() + " / " + a.getNumberOfSongs();
			result[i][Pos.ARTISTS_SPOTIFY.getId()] = a.getNumberOfSongs() - a.songsWithSpotify();
			i++;
		}
		return result;
	}
	
	public Set<String> getKeys(){
		return artists.keySet();
	}
	
	public List<Artist> getList(){
		return new ArrayList<Artist>(artists.values());
	}
	
	public List<Artist> getArtistsWithSubtitles(){
		return artists.values().stream()
							   .filter(a -> a.subTitles.length > 1)
							   .collect(Collectors.toList());
	}
	public List<String> getNames(){
		return artists.values().stream().map(a -> a.getBestTitle()).collect(Collectors.toList());
	}
	
	public Artist getByKeyName(String title){
		return artists.get(Library.TransformToBasicForm(title));
	}

	public List<Artist> getByName(String title){
		String finalTitle = Library.TransformToBasicForm(title);
		return artists.values().stream()
							   .filter(a -> Library.TransformToBasicForm(a.getBestTitle()).contains(finalTitle))
							   .collect(Collectors.toList());
	}
	
	public JSONArray toJSON(){
		JSONArray artistsArray = new JSONArray();
		for(Artist artist : artists.values()){
			artistsArray.put(artist.toJson());
		}
		return artistsArray;
	}
	
	public Artist getById(String id){
		return artists.get(id);
	}
	
	public int size(){
		return artists.size();
	}
}
