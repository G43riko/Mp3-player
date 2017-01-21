import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class Library {
	private HashMap<String, Artist> artists = new HashMap<String, Artist>();
	private HashMap<String, Song> songs = new HashMap<String, Song>();
	
	/*
	public List<Artist> artistsContainsAmpersant = new LinkedList<Artist>();
	public List<Artist> artistsContainsFeat = new LinkedList<Artist>();
	public List<Artist> artistsContainsFt = new LinkedList<Artist>();
	public List<Artist> artistsContainsAnd = new LinkedList<Artist>();
	*/
	public List<Artist> artistsPossibleJoined = new LinkedList<Artist>();
	
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
		   title.toLowerCase().contains(" and ")){
			artistsPossibleJoined.add(artist);
		}
		artists.put(title, artist);
		return artist;
	}
	public Song addSong(JSONObject object){
		String title = object.getString("title");
		if(songs.containsKey(title)){
			System.out.println("song " + title + " už existuje");
			return songs.get(title);
		}
		Song song = new Song(object);
		songs.put(title, song);
		//todo dorobiť pridanie autorov
		return song;
	}
	public Song addSong(File file, Artist ... artists){
		String title = file.getName().toLowerCase();
		if(songs.containsKey(title)){
			System.out.println("song " + title + " už existuje");
			return songs.get(title);
		}
		Song song = new Song(file);
		songs.put(title, song);
		//todo dorobiť pridanie autorov
		return song;
	}
	
	public String getArtistsString(){
		return String.join("\n", artists.values().stream()
												 .map(a -> a.toString() + "(" + a.getNumberOfSongs() + ")")
												 .collect(Collectors.toList()));
	}
	
	public void fromJson(JSONObject json){
		JSONArray songsArray = json.getJSONArray("songs");
//		System.out.println(json);
		for(int i=0 ; i<songsArray.length() ; i++){
			JSONObject songObject = songsArray.getJSONObject(i);
			Song song = addSong(songObject);
			JSONArray artists = songObject.getJSONArray("artists");
			for(int j=0 ; j<artists.length() ; j++){
				Artist artist = addArtist(artists.getString(j));
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
//		JSONArray artistsArray = new JSONArray();
//		for(Artist artist : artists.values()){
//			artistsArray.put(artist.toJson());
//		}
		JSONObject result = new JSONObject();
		result.put("songs", songsArray);
//		result.put("artists", artistsArray);
		
		return result;
	}
	
	
	public void postProcess(){
		System.out.println("test č. 1: " + showStats());
		//rozdeli na autorov ktorý sa rozdelili podla substringov v konštruktore 
		List<Artist> array = artists.values().stream().filter(a -> a.subTitles.length > 1).collect(Collectors.toList());
		for(int i=0 ; i<array.size() ; i++){
			divideArtist(array.get(i));
		}

		System.out.println("test č. 2: " + showStats());
		//prejde všetky ID artistov spolu s názov súboru a porovnáva ich 
		getArtistsSet().stream().forEach(artist -> {
			songs.values().stream().filter(a -> !a.containsArtists(artists.get(artist))).forEach(song -> {
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
	
	public Set<String> getArtistsSet(){
		return artists.keySet();
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
	public Object[][] getArtistData(){
		Object[][] result = new Object[artists.size()][3];
		int i=0;
		for(Artist a : artists.values()){
			result[i][0] = i + 1;
			result[i][1] = a.getTitle();
			result[i][2] = a.getNumberOfSongs();
			i++;
		}
		return result;
	}
	public Object[][] getTableData(){
		Object[][] result = new Object[songs.size()][9];
		int counter = 0;
		for(Song s : songs.values()){
			result[counter][1] = s.getStringArtists();//s.getTagArtist();
			result[counter][2] = s.getTagName();
			result[counter][3] = s.getYear();
			result[counter][4] = s.getGenre();
			result[counter][5] = Long.toString(s.getLength());
			result[counter][6] = Long.toString(s.getBitrate());
			result[counter][7] = s.getTitle();
			result[counter][8] = s;
			result[counter][0] = Integer.toString(counter++);
		}
		return result;
	}
	
	public int getNumberOfSongs(){return songs.size();}
	public int getNumberOfArtists(){return artists.size();}
	public Song getSong(String title){return songs.get(title.toLowerCase());}
	public Artist getArtist(String title){return artists.get(title.toLowerCase());}
//	public List<Artist> getArtistBySongs(){
//		return artists.values().stream()
//							   .sorted((b, a) -> a.getNumberOfSongs() - b.getNumberOfSongs())
//							   .collect(Collectors.toList());
//	}
	
	public String toString(){
		return String.join("\n", songs.values().stream().map(a -> a.toString()).collect(Collectors.toList()));
	}
}
