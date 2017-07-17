package com.API.spotify;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpotifyArtist {
	private String id;
	private String name;
	private List<SpotifySong> songs = new ArrayList<SpotifySong>();
	
	public SpotifyArtist(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return name + "[" + id + "] - " + songs.size() + "songs";
	}

	public void addSong(SpotifySong song){
		songs.add(song);
	}
	public List<SpotifySong> songsContainsString(Set<String> searchedSongs){
		List<SpotifySong> result = new ArrayList<SpotifySong>();
		for(SpotifySong s : songs){
			if(searchedSongs.contains(name.toLowerCase().trim())){
				result.add(s);
			}
		}
		return result;
	}
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public int getSongsSize(){
		return songs.size();
	}
}
