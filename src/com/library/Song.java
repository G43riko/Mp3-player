package com.library;
import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class Song {
	private String title;
	private List<Artist> artists = new LinkedList<Artist>();
	private String nameByTag = "";
	private String nameByTitle = "";
	private String name = "";
	private long length = 0;
	private String artistByTag = "";
	private String artistByTitle = "";
	private String spotifyId = "";
	private String spotifyName = "";
	private long spotifyDuration = 0;
	private String artist = "";
	private String year = "";
	private String genre = "";
	private String genreByTag = "";
	private String absoluePath = "";
	private long imported = 0;
	private long addSpotifyData = 0;
	public int frames;
	private int bitrate;
	public File file; 
	
	//CONTRUCTORS
	
	public Song(JSONObject object){
		title = object.getString("title");
		if(object.has("tagName"))
			nameByTag = object.getString("tagName");
		length = object.getLong("length");
		if(object.has("tagArtist"))
			artistByTag = object.getString("tagArtist");
		if(object.has("year"))
			year = object.getString("year");
		if(object.has("genre"))
			genre = object.getString("genre");
		absoluePath = object.getString("absoluePath");
		bitrate = object.getInt("bitrate");
		if(object.has("imported"))
			imported = object.getLong("imported");
//		file = new File(absoluePath);
	}
	
	public Song(File file){
		this.title = file.getName();
		this.absoluePath = file.getAbsolutePath();
		imported = System.currentTimeMillis();
	}
	
	public Song(String id, String name, long duration){
		spotifyId = id;
		spotifyName = name;
		spotifyDuration = duration;
		imported = addSpotifyData = System.currentTimeMillis();
	}
	
	
	//ADDERS
	
	public void addSpotifyData(String id, String name, long duration){
		spotifyId = id;
		spotifyName = name;
		spotifyDuration = duration;
		addSpotifyData = System.currentTimeMillis();
	}
	
	public void addArtist(Artist artist){
		if(!artists.contains(artist)){
			artists.add(artist);
		}
	}
	public void addArtists(List<Artist> newArtists){
		newArtists.stream()
				  .filter(a -> !artists.contains(a))
				  .forEach(a -> artists.add(a));
	}
	
	//OTHERS
	
	@Override
	public String toString() {
		return getStringArtists() + " - " + getTagName();
	}
	
	public boolean containsArtists(Artist artist){
		return artists.contains(artist);
	}
	
	public JSONObject toJson(){
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		for(Artist artist : artists){
			array.put(artist.getTitle());
		}
		result.put("artists", array);
		result.put("title", title);
		result.put("tagName", nameByTag);
		result.put("length", length);
		result.put("tagArtist", artistByTag);
		result.put("absoluePath", absoluePath);
		result.put("bitrate", bitrate);
		result.put("year", year);
		result.put("genre", genre);
		result.put("imported", imported);
		return result;
	}
	public void removeArtist(Artist artist){
		artists.remove(artist);
	}
	
	//GETTERS
	public String getBestName(){
		if(!spotifyName.isEmpty()){
			return spotifyName;
		}
		if(!nameByTag.isEmpty()){
			return nameByTag;
		}
		return name;
	}
	public String getStringArtists() {return String.join("; ", artists.stream().map(a -> a.getTitle()).collect(Collectors.toList()));}
	public String getTagName() {return nameByTag;}
	public long getLength() {return length;}
	public long getBitrate() {return bitrate;}
	public long getBestLength(){
		if(length > 0){
			return length;
		}
		if(spotifyDuration > 0){
			return spotifyDuration / 1000;
		}
		return 0;
	}
	public String getYear() {return year;}
	public String getGenre() {return genre;}
	public String getTitle() {return title;}
	public String getTagArtist() {return artistByTag;}
	public String getAbsolutePath() {return absoluePath;}
	public int getArtistCount() {return artists.size();}
	public String getLengthFormatted(){return getFormattedTime(getBestLength());}
	
	public static String getFormattedTime(long number){
		return (number / 60) + ":" + new DecimalFormat("00").format(number % 60);
	}
	
	//SETTERS
	
	public void setBitrate(int bitrate) {this.bitrate = bitrate;}
	public void setTagName(String name) {this.nameByTag = name;}
	public void setLength(long length) {this.length = length;}
	public void setYear(String year) {this.year = year;}
	public void setTagArtist(String tagArtist) {this.artistByTag = tagArtist;}
	public void setGenre(String genre) {this.genre = genre;}
	
}
