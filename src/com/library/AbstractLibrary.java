package com.library;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gui.Pos;

public class AbstractLibrary {
	//protected HashMap<String, Artist> artists = new HashMap<String, Artist>();
	//protected HashMap<String, Song> songs = new HashMap<String, Song>();
	protected SongManager songManager = new SongManager();
	protected ArtistManager artistManager = new ArtistManager();
	protected HashMap<String, Playlist> playlists = new HashMap<String, Playlist>();

	
	//ADDERS
	
	
	public Artist addArtist(String title){
		return artistManager.addArtist(title);
	}
	
	public Artist addArtist(JSONObject object){
		return artistManager.addArtist(object);
	}
	
	public Artist addArtist(String id, String name){
		return artistManager.addArtist(id, name);
	}
	
	public Song addSong(String id, String name, long duration){
		return songManager.addSong(id,  name,  duration);
	}
	
	public Song addSong(JSONObject object){
		return songManager.addSong(object);
	}
	
	public Song addSong(File file, Artist ... artists){
		return songManager.addSong(file,  artists);
	}
	
	

	public void fromJson(JSONObject json){
//			JSONArray artstArray = json.getJSONArray("artists");
//			for(int i=0 ; i<artstArray.length() ; i++){
//				addArtist(artstArray.getJSONObject(i));
//			}

		JSONArray songsArray = json.getJSONArray("songs");
		for(int i=0 ; i<songsArray.length() ; i++){
			JSONObject songObject = songsArray.getJSONObject(i);
			Song song = addSong(songObject);
			JSONArray artists = songObject.getJSONArray("artists");
			for(int j=0 ; j<artists.length() ; j++){
				JSONObject tmp = artists.optJSONObject(j);
				Artist artist = null;
				if(tmp == null){
					String tmpString = artists.optString(j, "");
					if(tmpString.isEmpty()){
						continue;
					}
					artist = addArtist(tmpString);
				}
				else{
					artist = addArtist(tmp);
				}
//					Artist artist = addArtist(artists.getString(j));
				song.addArtist(artist);
				artist.addSong(song);
			}
		}
	}
	
	public JSONObject toJson(){
		JSONObject result = new JSONObject();
		result.put("songs", songManager.toJSON());
		result.put("artists", artistManager.toJSON());
		return result;
	}
	
	
	public String toString(){
		return songManager.toString();
	}
	
	public String showStats() {
		String result = "songs: " + songManager.size();
		result += ", artists: " + artistManager.size();
		long artistsWithSubtitle = artistManager.getArtistsWithSubtitles().size();
		result += ", artists with subTitle: " + artistsWithSubtitle;
		
		long songsWithNoArtist = songManager.getSongsWithNoArtist().size();
		result += ", songs with zerro artist: " + songsWithNoArtist;
		
		return result;
	}
	
	
	//GETTERS
	
	/*public void showArtistKeys(){
		System.out.println(artists.keySet().stream()
										   .map(a -> artists.get(a).getNumberOfSongs() + "-" + artists.get(a).getBestTitle())
										   .collect(Collectors.toSet()));
	}*/
	
	public Object[][] getArtistData(){
		return artistManager.getData();
	}

	public String getArtistsString(){
		return artistManager.toString();
	}
	
	public Object[][] getTableData(){
		return songManager.getData();
	}
	
	public Set<String> getSongsString(){
		return songManager.getKeySet();
	}
	
	public int getNumberOfSongs(){return songManager.size();}
	public int getNumberOfArtists(){return artistManager.size();}
	public Song getSong(String title){return songManager.getByName(title);}
	public Song getSongById(String title){return songManager.getById(title);}
	public Artist getArtist(String title){return artistManager.getByKeyName(title);}
	public List<Artist> getArtistsList(){return artistManager.getList();}
	public Set<String> getSimilarArtists(String title){
		return artistManager.getKeys().stream().filter(a -> a.toLowerCase().trim().contains(title.toLowerCase().trim())).collect(Collectors.toSet());
	}

	public Set<Song> getSongs(){
		return songManager.getSongsSet();
	}
	public Set<String> getSongNames(){
		return songManager.getSongsNames();
	}
}
