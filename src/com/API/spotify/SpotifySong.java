package com.API.spotify;

import java.util.ArrayList;
import java.util.List;


public class SpotifySong {
	public long duration;
	public String id;
	public String name;
	public String isrc;
	public String ean;
	public String preview;
	public String upc;
	public List<SpotifyArtist> artists = new ArrayList<SpotifyArtist>();
	@Override
	public String toString() {
		return name + "[" + id + "(" + isrc + ")](" + duration + ")";
	}
	
	
}
