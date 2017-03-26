package com.library;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

public class Artist {
	private String title;
	public boolean deleted = false;
	public String[] subTitles;
	private String spotifyId = "";
	private String spotifyName = "";
	private long addSpotifyData = 0;
	private long imported = 0;
	private List<Song> songs = new LinkedList<Song>();
	private final static String DELIMITER = "#################";
	
	public Artist(String title){
		this.title = title;
		String subResult = title.toLowerCase();
		subResult = subResult.replace(";", DELIMITER);
		subResult = subResult.replace("&", DELIMITER);
		subResult = subResult.replace(" and", DELIMITER);
		subResult = subResult.replace("feat.", DELIMITER);
		subResult = subResult.replace("feat", DELIMITER);
		subResult = subResult.replace(" ft", DELIMITER);
		subResult = subResult.replace(" x ", DELIMITER);
		subResult = subResult.replace(" vs ", DELIMITER);
		subTitles = subResult.split(DELIMITER);
		
		if(subTitles.length > 1){
			for(int i=0 ; i<subTitles.length ; i++)
				subTitles[i] = subTitles[i].trim(); 
		}
		imported = System.currentTimeMillis();
	}
	public Artist(String spotifyId, String spotifyName){
		this.spotifyId = spotifyId;
		this.spotifyName = spotifyName;
		subTitles = new String[]{spotifyName};
		addSpotifyData = imported = System.currentTimeMillis();
	}
	public void setSpotifyData(String spotifyId, String spotifyName){
		this.spotifyId = spotifyId;
		this.spotifyName = spotifyName;
		addSpotifyData = System.currentTimeMillis();
	}

	public Object[][] getTableData(){
		Object[][] result = new Object[songs.size()][5];
		int counter = 0;
		for(Song s : songs){
			result[counter][1] = s.getTagName();
			result[counter][2] = s.getGenre();
			result[counter][3] = Long.toString(s.getLength());
			result[counter][4] = s;
			result[counter][0] = Integer.toString(counter++);
		}
		return result;
	}
	public void addSong(Song song){
		songs.add(song);
	}

	public void addSongs(List<Song> newSongs){
		songs.addAll(newSongs);
	}
	
	List<Song> getSongs(){
		return songs;
	}
	
	public int getNumberOfSongs(){
		return songs.size();
	}
	
	public String toString(){
		return getTitle();
	}
	public String getTitle(){
		return title;
	}
	public JSONObject toJson(){
		JSONObject result = new JSONObject();
		result.put("title", title);
		result.put("soundNumber", songs.size());
		if(subTitles.length > 1){
			result.put("subtitles", subTitles);
		}
		return result;
	}
}
