package com.library;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.gui.Pos;

public class Artist {
	private final static String DELIMITER = "#################";
	
	private String 		title			= "";
	private boolean 	removed 		= false;
	public String[] 	subTitles;
	private String 		spotifyId 		= "";
	private String 		spotifyName 	= "";
	private long 		addSpotifyData 	= 0;
	private long 		imported 		= 0;
	private List<Song> 	songs 			= new LinkedList<Song>();
	
	public static Artist merge(Artist a1, Artist a2){
		if(a1.addSpotifyData > 0){
			a2.spotifyId = a1.spotifyId;
			a2.spotifyName = a1.spotifyName;
			a2.addSpotifyData = a1.addSpotifyData;
			
			for(Song s : a1.songs){
				if(!s.containsArtist(a2)){
					a2.addSong(s);
					s.addArtist(a2);
				}
				s.removeArtist(a1);
				a1.removed = true;
			}
			return a2;
		}
		
		if(a2.addSpotifyData > 0){
			a1.spotifyId = a2.spotifyId;
			a1.spotifyName = a2.spotifyName;
			a1.addSpotifyData = a2.addSpotifyData;
			
			for(Song s : a2.songs){
				if(!s.containsArtist(a1)){
					a1.addSong(s);
					s.addArtist(a1);
				}
				s.removeArtist(a2);
				a2.removed = true;
			}
			return a1;
		}
		return null;
	};
	
	public static void subTitle(Artist a, String subTitle){
		if(a.title == null){
			return;	
		}
		String subResult = a.title.toLowerCase();
		
		subResult = subResult.replace(";", DELIMITER);
		subResult = subResult.replace("&", DELIMITER);
		subResult = subResult.replace(" and", DELIMITER);
		subResult = subResult.replace("feat.", DELIMITER);
		subResult = subResult.replace("feat", DELIMITER);
		subResult = subResult.replace("/", DELIMITER);
		subResult = subResult.replace(" ft", DELIMITER);
		subResult = subResult.replace(" x ", DELIMITER);
		subResult = subResult.replace(" vs ", DELIMITER);
//		if(subTitle != null && !subTitle.isEmpty()){
//			subResult = subResult.replace(subTitle, DELIMITER);
//		}
		a.subTitles = subResult.split(DELIMITER);
		
		if(a.subTitles.length > 1){
			for(int i=0 ; i<a.subTitles.length ; i++)
				a.subTitles[i] = a.subTitles[i].trim(); 
		}
	}
	
	public Artist(JSONObject object){
		subTitles = new String[0];
		if(object.has("title")){
			title = object.getString("title");
		}
		
		if(object.has("spotifyId")){
			spotifyId = object.getString("spotifyId");
		}
		
		if(object.has("spotifyName")){
			spotifyName = object.getString("spotifyName");
		}
		
		if(object.has("removed")){
			removed = object.getBoolean("removed");
		}

		if(object.has("addSpotifyData")){
			addSpotifyData = object.getLong("addSpotifyData");
		}
		if(object.has("imported")){
			imported = object.getLong("imported");
		}
	}
	
	public Artist(String title){
		this.title = title;
		subTitle(this, null);
		imported = System.currentTimeMillis();
	}
	
	public Artist(String spotifyId, String spotifyName){
		setSpotifyData(spotifyId, spotifyName);
		subTitles = new String[]{spotifyName};
	}
	
	public void removeSong(Song s){
		songs.remove(s);
	}
	
	public boolean is(String title){
		return title.toLowerCase().trim().equals(this.title.toLowerCase().trim()) || title.toLowerCase().trim().equals(spotifyName.toLowerCase().trim());
	}
	
	//ADDERS
	
	public void addSong(Song song){
		if(!songs.contains(song)){
			songs.add(song);
		}
	}

	public void addSongs(List<Song> newSongs){
		newSongs.stream()
				.filter(a -> !songs.contains(a))
				.forEach(songs::add);
	}
	
	//TOTERS
	
	public String toString(){
		return getBestTitle();
	}
	
	public JSONObject toJson(){
		JSONObject result = new JSONObject();
		
		if(subTitles.length > 1){
			result.put("subtitles", subTitles);
		}
		
		result.put("title", title);
		result.put("removed", removed);
		result.put("imported", imported);
		result.put("spotifyId", spotifyId);
		result.put("spotifyName", spotifyName);
		result.put("soundNumber", songs.size());
		result.put("addSpotifyData", addSpotifyData);
		
		return result;
	}

	//GETTERS
	
	public String getTitle(){return title;}
	public List<Song> getSongs(){return songs;}
	public String getSpotifyId(){return spotifyId;}
	public int getNumberOfSongs(){return (int)songs.stream().filter(a -> !a.isRemoved()).count();}
//	public int getNumberOfSongs(){return songs.size();}
	public boolean hasSpotifyData(){return addSpotifyData > 0;}
	public boolean isRemoved(){return removed;}
	public Object[][] getTableData(){
		Object[][] result = new Object[getNumberOfSongs()][Pos.ArtistTitlesSize()];
		int counter = 0;
		for(Song s : songs){
			if(s.isRemoved()){
				continue;
			}
			result[counter][Pos.ARTIST_NAME.getId()] = s.getBestName();
			result[counter][Pos.ARTIST_GENRE.getId()] = s.getBestGenre();
			result[counter][Pos.ARTIST_LENGTH.getId()] = Long.toString(s.getBestLength());
			result[counter][Pos.ARTIST_SONG.getId()] = s;
			result[counter][Pos.ARTIST_ID.getId()] = Integer.toString(counter++);
		}
		return result;
	}
	public String getBestTitle(){
		if(!spotifyName.isEmpty()){
			return spotifyName;
		}
		if(!title.isEmpty()){
			return title;
		}
		return "unknown";
	}
	
	@Override
	public int hashCode() {
		return getBestTitle().hashCode();
	}
	public boolean equals(Object obj) {
		if(!(obj instanceof Artist)){
			return false;
		}
		return getBestTitle().equals(((Artist)obj).getBestTitle());
	}
	
	//SETTERS
	
	public void setSpotifyData(String spotifyId, String spotifyName){
		this.spotifyId = spotifyId;
		this.spotifyName = spotifyName;
		addSpotifyData = System.currentTimeMillis();
	}
	
}
