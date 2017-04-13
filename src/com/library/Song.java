package com.library;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.xml.internal.ws.binding.SOAPBindingImpl;

public class Song {
	private final static String DEFAULT_NAME	= "Unknown";
	private final static String DEFAULT_YEAR	= "0000";
	private final static String DEFAULT_GENRE	= "NONE";
	private final static long 	DEFAULT_LENGTH	= 0;
	
	private Map<String, String> historicalData	= new HashMap<String, String>();
	private String 				title			= null; //názov súboru
	private List<Artist>		artists 		= new LinkedList<Artist>();
	private String 				spotifyName 	= "";	//názov podla spotify
	private String 				nameByTag 		= "";	//názov podla tagu
	private String 				nameByTitle 	= "";	//názov vyrátaný z názvu súboru
	private String 				name 			= "";	//názov zadaný používatelom
	private long 				length 			= 0;
	private String 				artistByTag 	= "";
	private String 				spotifyId 		= "";
	private long 				spotifyDuration = 0;
	private String 				year 			= "";
	private String 				yearByTag 		= "";
	private String 				genre 			= "";
	private String 				genreByTag 		= "";
	private String				absoluePath 	= "";	//absolutna cesta
	private long 				imported 		= 0;	//kedy bolo pridane
	private long 				played 			= 0;	//pocet prehratí
	private long 				addSpotifyData 	= 0;	//datum pridania spotify dát
	public int 					frames			= 0;
	private int 				bitrate			= 0;
	private boolean 			removed			= false;
//	public File file 				= null; 
//	private String artist 			= "";
//	private String artistByTitle 	= "";
	
	//CONTRUCTORS
	
	public void setHistoricalData(String key, String value){
		if(historicalData.containsKey(key)){
			value = historicalData.get(key) + "; " + value;
		}
		historicalData.put(key, value);
	}
	
	
	public static Song merge(Song s1, Song s2){
		if(s1.addSpotifyData > 0){
			s2.spotifyDuration = s1.spotifyDuration;
			s2.spotifyId = s1.spotifyId;
			s2.spotifyName = s1.spotifyName;
			s2.addSpotifyData = s1.addSpotifyData;
			
			for(Artist a : s1.artists){
				if(!s2.containsArtists(a)){
					s2.addArtist(a);
					a.addSong(s2);
				}
				s1.removeArtist(a);
			}
			s1.removed = true;
			return s2;
		}
		else if(s2.addSpotifyData > 0){
			s1.spotifyDuration = s2.spotifyDuration;
			s1.spotifyId = s2.spotifyId;
			s1.spotifyName = s2.spotifyName;
			s1.addSpotifyData = s2.addSpotifyData;
			for(Artist a : s2.artists){
				if(!s1.containsArtists(a)){
					s1.addArtist(a);
					a.addSong(s1);
				}
				s2.removeArtist(a);
			}
			s2.removed = true;
			return s1;
		}
		return null;
	}
	
	
	public Song(JSONObject object){
		if(object.has("title")){
			title = object.getString("title");
		}
		if(object.has("removed")){
			removed = object.getBoolean("removed");
		}
		if(object.has("tagName")){
			nameByTag = object.getString("tagName");
		}
		length = object.getLong("length");
		if(object.has("tagArtist")){
			artistByTag = object.getString("tagArtist");
		}
		if(object.has("year")){
			year = object.getString("year");
		}
		if(object.has("genre")){
			genre = object.getString("genre");
		}

		if(object.has("played")){
			played = object.getLong("played");
		}

		if(object.has("name")){
			name = object.getString("name");
		}
		

		if(object.has("spotifyId")){
			spotifyId = object.getString("spotifyId");
		}
		if(object.has("spotifyName")){
			spotifyName = object.getString("spotifyName");
		}
		if(object.has("spotifyDuration")){
			spotifyDuration = object.getLong("spotifyDuration");
		}
		if(object.has("addSpotifyData")){
			addSpotifyData = object.getLong("addSpotifyData");
		}
		
		if(object.has("absoluePath")){
			absoluePath = object.getString("absoluePath");
		}

		if(object.has("bitrate")){
			bitrate = object.getInt("bitrate");
		}
		if(object.has("imported")){
			imported = object.getLong("imported");
		}
		if(object.has("frames")){
			frames = object.getInt("frames");
		}
//		file = new File(absoluePath);
	}
	
	public Song(File file){
		this.title = file.getName();
		this.absoluePath = file.getAbsolutePath();
		imported = System.currentTimeMillis();
	}
	
	public Song(String id, String name, long duration){
		addSpotifyData(id, name, duration);
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
				  .forEach(artists::add);
	}
	
	//OTHERS
	
	@Override
	public String toString() {
		return getStringArtists() + " - " + getBestName();
	}
	public boolean equals(Object obj) {
		if(!(obj instanceof Song)){
			return false;
		}
		Song song = (Song)obj;
		return getBestName().equals(song.getBestName()) && getBestLength() == song.getBestLength() && spotifyId == song.spotifyId;
	}
	public boolean containsArtists(Artist artist){
		return artists.contains(artist);
	}

	public boolean hasSpotifyData(){return addSpotifyData > 0;}
	public JSONObject toJson(){
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		for(Artist artist : artists){
			array.put(artist.toJson());
		}
		result.put("name", name);
		result.put("year", year);
		result.put("genre", genre);
		result.put("played", played);
		result.put("title", title);
		result.put("length", length);
		result.put("artists", array);
		result.put("frames", frames);
		result.put("bitrate", bitrate);
		result.put("removed", removed);
		result.put("tagName", nameByTag);
		result.put("imported", imported);
		result.put("spotifyId", spotifyId);
		result.put("tagArtist", artistByTag);
		result.put("absoluePath", absoluePath);
		result.put("spotifyName", spotifyName);
		result.put("addSpotifyData", addSpotifyData);
		result.put("spotifyDuration", spotifyDuration);
		return result;
	}
	public void removeArtist(Artist artist){
		artists.remove(artist);
	}
	
	public boolean containsArtist(Artist a){
		return artists.contains(a);
	}
	
	//GETTERS
	
	public String getSource(){
		String result = spotifyId.isEmpty() ? "" : "S";
		if(absoluePath != null && !absoluePath.isEmpty()){
			result += "L";
		}
		return result;
	}
	
	public String getBestName(){
		if(!name.isEmpty()){
			return name;
		}
		if(!spotifyName.isEmpty()){
			return spotifyName;
		}
		if(!nameByTag.isEmpty()){
			return nameByTag;
		}
		if(!nameByTitle.isEmpty()){
			return nameByTitle;
		}
		return DEFAULT_NAME;
	}

	
	public long getBestLength(){
		if(length > 0){
			return length;
		}
		if(spotifyDuration > 0){
			return spotifyDuration / 1000;
		}
		return DEFAULT_LENGTH;
	}
	
	public String getBestYear(){
		if(!year.isEmpty()){
			return year;
		}
		else if(!yearByTag.isEmpty()){
			return yearByTag;
		}
		return DEFAULT_YEAR;
	}
	
	public String getBestGenre(){
		if(!genre.isEmpty()){
			return genre;
		}
		else if(!genreByTag.isEmpty()){
			return genreByTag;
		}
		return DEFAULT_GENRE;
	}
	public String getYear() {return year;}
	public String getGenre() {return genre;}
	public String getTitle() {return title;}
	public String getName() {return name;}
	public long getLength() {return length;}
	public long getBitrate() {return bitrate;}
	public boolean isRemoved(){return removed;}
	public String getTagName() {return nameByTag;}
	public List<Artist> getArtists(){return artists;}
	public String getTagArtist() {return artistByTag;}
	public int getArtistCount() {return (int)artists.stream().filter(a -> !a.isRemoved()).count();}
//	public int getArtistCount() {return artists.size();}
	public String getAbsolutePath() {return absoluePath;}
	public boolean hasArtist(Artist artist){return artists.contains(artist);}
	public String getLengthFormatted(){return getFormattedTime(getBestLength());}
	public static String getFormattedTime(long number){return (number / 60) + ":" + new DecimalFormat("00").format(number % 60);}
	public String getStringArtists() {return String.join("; ", artists.stream().filter(a -> !isRemoved()).map(a -> a.getBestTitle()).collect(Collectors.toList()));}

	public String getSpotifyName() {return spotifyName;}
	public String getSpotifyId() {return spotifyId;}
	public long getSpotifyDuration() {return spotifyDuration;}
	
	public boolean hasArtist(String artist){
		for(Artist a : artists){
			if(a.is(artist)){
				return true;
			}
		}
		return false;
	}
	
	//SETTERS
	
	public void setBitrate(int bitrate) {this.bitrate = bitrate;}
	
	public void setName(String name) {this.name = name;}
	public void setTagName(String name) {this.nameByTag = name;}
	public void setLength(long length) {this.length = length;}
	public void setYear(String year) {this.year = year;}
	public void setTagArtist(String tagArtist) {this.artistByTag = tagArtist;}
	public void setGenre(String genre) {this.genre = genre;}

	public void remove() {
		removed = true;
	}
	
}
