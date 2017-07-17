package com.library;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gui.Pos;

public class SongManager {
	private HashMap<String, Song> songs = new HashMap<String, Song>();
	
	public Song addSong(String id, String name, long duration){
		if(songs.containsKey(id)){
			return songs.get(id);
		}
		for(Song song : songs.values()){
			if(song.getBestName().toLowerCase().replace(".mp3", "").trim().equals(name.toLowerCase().trim())){
				song.addSpotifyData(id, name, duration);
				return song;
			}
		}
		
		Song song = new Song(id, name, duration);
		songs.put(id, song);
		return song;
	}
	
	public Song addSong(JSONObject object){
		Song song;
		if(object.has("title")){
			String title = object.getString("title");
			if(songs.containsKey(title)){
				return songs.get(title);
			}
			song = new Song(object);
			songs.put(title.toLowerCase(), song);
			//todo dorobiť pridanie autorov
		}
		else{
			String title = object.getString("spotifyId");
			if(songs.containsKey(title)){
				return songs.get(title);
			}
			song = new Song(object);
			songs.put(title, song);
		}
		return song;
	}
	
	public Song addSong(File file, Artist ... artists){
		String title = file.getName().toLowerCase();
		if(songs.containsKey(title)){
//				System.out.println("song " + title + " už existuje");
			return songs.get(title);
		}
		Song song = new Song(file);
		songs.put(title, song);
		//todo dorobiť pridanie autorov
		return song;
	}
	JSONArray toJSON(){
		JSONArray songsArray = new JSONArray();
		for(Song song : songs.values()){
			songsArray.put(song.toJson());
		}
		return songsArray;
	}
	public String toString(){
		return String.join("\n", songs.values().stream().map(a -> a.toString()).collect(Collectors.toList()));
	}

	public List<Song> getSongsWithNoArtist(){
		return songs.values().stream().filter(a -> a.getArtistCount() == 0).collect(Collectors.toList());
	}
	public Set<String> getSongsNames(){
		return songs.values().stream().map(a -> a.getBestName()).collect(Collectors.toSet());
	}
	
	public Set<String> getKeySet(){
		return songs.keySet();
	}
	
	public Set<Song> getSongsSet(){
		return new HashSet<Song>(songs.values());
	}
	public List<Song> getNotRemovedSongs(){
		return songs.values().stream().filter(a -> !a.isRemoved()).collect(Collectors.toList());
	}
	
	public Collection<Song> getValues(){
		return songs.values();
	}
	
	public Object[][] getData(){
		Object[][] result = new Object[getNotRemovedSongs().size()][Pos.SongTitlesSize()];
		
		int counter = 0;
		for(Song s : songs.values()){
			if(s.isRemoved()){
				continue;
			}
			result[counter][Pos.SONGS_ID.getId()]		= Integer.toString(counter);
			result[counter][Pos.SONGS_SONG.getId()] 	= s;
			result[counter][Pos.SONGS_NAME.getId()]		= s.getBestName();//s.getTagName();
			result[counter][Pos.SONGS_YEAR.getId()]		= s.getBestYear();
			result[counter][Pos.SONGS_TITLE.getId()] 	= s.getTitle();
			result[counter][Pos.SONGS_GENRE.getId()]	= s.getBestGenre();
			result[counter][Pos.SONGS_SOURCE.getId()] 	= s.getSource();
			result[counter][Pos.SONGS_LENGTH.getId()] 	= s.getLengthFormatted();
			result[counter][Pos.SONGS_ARTIST.getId()] 	= s.getStringArtists();//s.getTagArtist();
			result[counter][Pos.SONGS_BITRATE.getId()] 	= Long.toString(s.getBitrate());
			counter++;
		}
		return result;
	}
	
	public int size(){
		return songs.size();
	}
	
	public Song getByName(String title){
		return songs.get(Library.TransformToBasicForm(title));
	}
	public Song getById(String title){
		return songs.get(title);
	}
}
