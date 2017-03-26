package com.library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONObject;

public class Playlist {
	private String title;
	private long created;
	
	private List<Song> songs = new ArrayList<Song>();
	
	public Playlist(JSONObject object){
		
	};
	public void addSong(Song song){
		//TODO čo ak existuje
		songs.add(song);
	};
	public void removeSong(Song song){
		songs.remove(song);
	};
	public int size(){
		return songs.size();
	};
	
	public int makeUnique(){
		int startSize = songs.size();
		songs = new ArrayList<Song>(new HashSet<Song>(songs));
		return startSize - songs.size();
	}
	public JSONObject toJSON(){
		JSONObject result = new JSONObject();
		//TODO dorobiť
		return result;
	}
	public String toString(){
		return title + "[" + created + "]: " + songs.size();  
	};
	public long getCreated(){
		return created;
	};
	public String getTitle(){
		return title;
	};
	public void setTitle(String title){
		this.title = title;
	};
}
