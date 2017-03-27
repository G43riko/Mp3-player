package com.API.spotify;

import java.util.ArrayList;
import java.util.List;

public class SpotifyArtist {
	public String id;
	public String name;
	public List<SpotifySong> songs = new ArrayList<SpotifySong>();
	
	@Override
	public String toString() {
		return name + "[" + id + "] - " + songs.size() + "songs";
	}
}
