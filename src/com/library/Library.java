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
import com.utils.FileManager;
import com.utils.FileSearcher;
import com.utils.FileUtils;

public class Library {
	private HashMap<String, Artist> artists = new HashMap<String, Artist>();
	private HashMap<String, Song> songs = new HashMap<String, Song>();
	private HashMap<String, Playlist> playlists = new HashMap<String, Playlist>();

	public List<Artist> artistsPossibleJoined = new LinkedList<Artist>();
	
	//ADDERS
	
	public void addSpotifyData(Object[][] data){
		for(int i=0 ; i<data.length ; i++){
			Song song = addSong((String)data[i][0], (String)data[i][1], (long)data[i][2]);
			HashMap<String, String> songArtists = (HashMap<String, String>)data[i][3];
			for(Entry<String, String> artistData : songArtists.entrySet()){
				Artist artist = addArtist(artistData.getKey(), artistData.getValue());
				song.addArtist(artist);
				artist.addSong(song);
			}
		}
	}
	
	public Artist addArtist(String title){
		title = title.toLowerCase();
		if(artists.containsKey(title)){
			return artists.get(title);
		}
		Artist artist = new Artist(title);
		if(title.toLowerCase().contains("feat") || 
		   title.contains(";") || 
		   title.contains("&") || 
		   title.toLowerCase().contains(" vs ") || 
		   title.toLowerCase().contains(" ft") ||
		   title.toLowerCase().contains("feat.") ||
		   title.toLowerCase().contains(" and ")){
			artistsPossibleJoined.add(artist);
		}
		artists.put(title, artist);
		return artist;
	}
	
	public Artist addArtist(JSONObject object){
		Artist artist = null;
		if(object.has("spotifyId")){
			artist = artists.get(object.getString("spotifyId"));
			if(artist != null){
				return artist;
			}
		}
		if(object.has("title")){
			artist = artists.get(object.getString("title"));
			if(artist != null){
				return artist;
			}
		}
		
		artist = new Artist(object);
		if(artist.hasSpotifyData()){
			artists.put(artist.getSpotifyId(), artist);
		}
		else{
			artists.put(artist.getBestTitle(), artist);
		}
		return artist;
	}
	
	public Artist addArtist(String id, String name){
		Artist artist = null;
		
		//ak existuje podla nazvu
		if(artists.containsKey(name.toLowerCase())){
			artist = artists.get(name.toLowerCase());
			artist.setSpotifyData(id, name);
			return artist;
		}
		
		//ak existuje podla id
		if(artists.containsKey(id)){
			artist = artists.get(id);
			artist.setSpotifyData(id, name);
			return artist;
		}
		artist = new Artist(id, name);
		artists.put(name.toLowerCase(), artist);
		return artist;
	}
	
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
//			System.out.println("song " + title + " už existuje");
			return songs.get(title);
		}
		Song song = new Song(file);
		songs.put(title, song);
		//todo dorobiť pridanie autorov
		return song;
	}
	
	//IMPORTERS
	
	public void importFilesFromDirectory(String path){
		List<String> songs = FileSearcher.searchDir(path).stream().filter(a -> a.contains(".mp3")).collect(Collectors.toList());
		
		FileUtils.createLibrary(this, songs);
		
		postProcess();
	}
	
	public void importFileFromFile(String fileName){
		fromJson(new JSONObject(FileManager.loadFromFile(fileName)));
	}
	
	public void fromJson(JSONObject json){
//		JSONArray artstArray = json.getJSONArray("artists");
//		for(int i=0 ; i<artstArray.length() ; i++){
//			addArtist(artstArray.getJSONObject(i));
//		}

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
//				Artist artist = addArtist(artists.getString(j));
				song.addArtist(artist);
				artist.addSong(song);
			}
		}
	}
	
	public JSONObject toJson(){
		JSONArray songsArray = new JSONArray();
		for(Song song : songs.values()){
			songsArray.put(song.toJson());
		}
		JSONArray artistsArray = new JSONArray();
		for(Artist artist : artists.values()){
			artistsArray.put(artist.toJson());
		}
		JSONObject result = new JSONObject();
		result.put("songs", songsArray);
		result.put("artists", artistsArray);
		
		return result;
	}
	
	
	public void divideArtists(){
		List<Artist> array = artists.values().stream().filter(a -> a.subTitles.length > 1).collect(Collectors.toList());
		for(int i=0 ; i<array.size() ; i++){
			divideArtist(array.get(i));
		}
	}
	
	public void postProcess(){
		System.out.println("test č. 1: " + showStats());
		//rozdeli na autorov ktorý sa rozdelili podla substringov v konštruktore 
		divideArtists();
		List<Artist> array;
		System.out.println("test č. 2: " + showStats());
		//prejde všetky ID artistov spolu s názov súboru a porovnáva ich 
		artists.keySet().stream().forEach(artist -> {
			songs.values().stream().filter(a -> !a.containsArtists(artists.get(artist)) && a.getTitle() != null).forEach(song -> {
				String searchA = song.getTitle().toLowerCase();
				String searchB = artist.toLowerCase();
				if(searchA.contains(" " + searchB + " ") || 
				   searchA.startsWith(searchB + " ") ||
				   searchA.endsWith(" " + searchB)){
					song.addArtist(artists.get(artist));
					artists.get(artist).addSong(song);
				}
			});
		});
		System.out.println("test č. 3: " + showStats());
		//TOD prejde všetkych znamych autorov a porovnava ich z nazvom súboru
		
		for(Song s : songs.values()){
			String title = s.getTitle();
			if(title == null){
				continue;
			}
			int counter = 0;
			for( int i=0; i<title.length(); i++ ) {
			    if( title.charAt(i) == '-' ) {
			        counter++;
			        if(counter > 1){
			        	break;
			        }
			    } 
			}
			if(counter == 1){
				String[] substrings = title.split("-");
				Artist artist = addArtist(substrings[0].trim());
				s.addArtist(artist);
				s.setTagName(substrings[1].replace(".mp3", "").trim());//TODO nastavuje name nie tagName
				artist.addSong(s);
			}
		}
		array = artists.values().stream().filter(a -> a.subTitles.length > 1).collect(Collectors.toList());
		for(int i=0 ; i<array.size() ; i++){
			divideArtist(array.get(i));
		}
		System.out.println("test č. 4: " + showStats());
		/*
		for(Artist a : artists.values()){
			a.getSongs()
		}
		*/
		
	}
	public void divideArtist(Artist artist){
		List<Song> songs = artist.getSongs();
		List<Artist> newArtists = new LinkedList<Artist>();
		
		for(int i=0 ; i<artist.subTitles.length ; i++){
			newArtists.add(i, addArtist(artist.subTitles[i]));//vytvory noveho interpreta
			newArtists.get(i).addSongs(songs);//prida mu všetky pesničky od stareho
			artists.put(artist.subTitles[i], newArtists.get(i));
		}
		
		for(int i=0 ; i<songs.size() ; i++){
			songs.get(i).removeArtist(artist);//vymaže s pesničky stareho interpreta
			songs.get(i).addArtists(newArtists);//prida pesničkam novych interpretov
		}
		artists.remove(artist.getTitle());
	}
	

	public String toString(){
		return String.join("\n", songs.values().stream().map(a -> a.toString()).collect(Collectors.toList()));
	}
	
	public String showStats() {
		String result = "songs: " + songs.size();
		result += ", artists: " + artists.size();
		long artistsWithSubtitle = artists.values().stream().filter(a -> a.subTitles.length > 1).count();
		result += ", artists with subTitle: " + artistsWithSubtitle;
		
		long songsWithNoArtist = songs.values().stream().filter(a -> a.getArtistCount() == 0).count();
		result += ", songs with zerro artist: " + songsWithNoArtist;
		
		return result;
	}


	public void changeSongArtist(Song s, String value) {
		String[] artists = value.split(";");
		System.out.println(s.getStringArtists() + " -> " + value);
		int counter = 0;
		for(String artist : artists){
			if(!s.hasArtist(artist)){
				Artist artistNew = this.artists.get(artist.trim());
				if(artistNew == null){
					artistNew = this.artists.get(artist.toLowerCase().trim());
				}
				if(artistNew == null){
					artistNew = new Artist(artist);
				}
				
				s.addArtist(artistNew);
				artistNew.addSong(s);
			}
			
			counter++;
		}
		if(s.getArtistCount() != counter){
//			s.getArtists().stream().filter(a -> !value.toLowerCase().contains(a.getBestTitle())).forEach(a -);)
			List<Artist> artistsList = s.getArtists();
			for(int i=0 ; i<artistsList.size() ; i++){
				Artist a = artistsList.get(i);
				if(!value.toLowerCase().contains(a.getBestTitle())){
					s.removeArtist(a);
					a.removeSong(s);
				}
			}
//			for(Artist a : s.getArtists()){
//				if(!value.toLowerCase().contains(a.getBestTitle())){
//					s.removeArtist(a);
//					a.removeSong(s);
//				}
//			}
		}
	}
	
	//GETTERS
	public Object[][] getArtistData(){
		Object[][] result = new Object[artists.size()][Pos.ArtistsTitlesSize()];
		int i=0;
		for(Artist a : artists.values()){
			result[i][Pos.ARTISTS_ID.getId()] = i + 1;
			result[i][Pos.ARTISTS_NAME.getId()] = a.getBestTitle();
			result[i][Pos.ARTISTS_SONGS.getId()] = a.getNumberOfSongs();
			result[i][Pos.ARTISTS_INFO.getId()] = a;
//			result[i][Pos.ARTISTS_SPOTIFY.getId()] = a.hasSpotifyData() ? "yes" : "no";
			result[i][Pos.ARTISTS_SPOTIFY.getId()] = a.songsWithSpotify() + " / " + a.getNumberOfSongs();
			result[i][Pos.ARTISTS_SPOTIFY.getId()] = a.getNumberOfSongs() - a.songsWithSpotify();
			i++;
		}
		return result;
	}

	public String getArtistsString(){
		return String.join("\n", artists.values().stream()
												 .map(a -> a.toString() + "(" + a.getNumberOfSongs() + ")")
												 .collect(Collectors.toList()));
	}
	
	public Object[][] getTableData(){
		Object[][] result = new Object[(int)songs.values().stream().filter(a -> !a.isRemoved()).count()][Pos.SongTitlesSize()];
		
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
	
	public int getNumberOfSongs(){return songs.size();}
	public int getNumberOfArtists(){return artists.size();}
	public Song getSong(String title){return songs.get(title.toLowerCase());}
	public Song getSongById(String title){return songs.get(title);}
	public Artist getArtist(String title){return artists.get(title.toLowerCase());}
	public List<Artist> getArtistsList(){return new ArrayList<Artist>(artists.values());}
	public Set<String> getSimilarArtists(String title){
		return artists.keySet().stream().filter(a -> a.toLowerCase().trim().contains(title.toLowerCase().trim())).collect(Collectors.toSet());
	}
	public Set<String> getSongsString(){
		return songs.keySet();
	}

	public Set<Song> getSongs(){
		return new HashSet<Song>(songs.values());
	}
	public Set<String> getSongNames(){
		return songs.values().stream().map(a -> a.getTagName()).collect(Collectors.toSet());
	}
}
