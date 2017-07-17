package com.library;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.utils.FileManager;
import com.utils.FileSearcher;
import com.utils.FileUtils;

public class Library extends AbstractLibrary{
	public static String TransformToBasicForm(String key){
		return key.toLowerCase().trim();
	}
	//IMPORTERS

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
	
	public void importFilesFromDirectory(String path){
		List<String> songs = FileSearcher.searchDir(path).stream()
														 .filter(a -> a.contains(".mp3"))
														 .collect(Collectors.toList());
		
		FileUtils.createLibrary(this, songs);
		
		postProcess();
	}
	
	public void importFileFromFile(String fileName){
		fromJson(new JSONObject(FileManager.loadFromFile(fileName)));
	}
	
	
	public void divideArtists(){
		List<Artist> array = artistManager.getArtistsWithSubtitles();
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
		artistManager.getKeys().stream().forEach(artist -> {
			songManager.getValues().stream().filter(a -> !a.containsArtists(artistManager.getById(artist)) && a.getTitle() != null).forEach(song -> {
				String searchA = song.getTitle().toLowerCase();
				String searchB = artist.toLowerCase();
				if(searchA.contains(" " + searchB + " ") || 
				   searchA.startsWith(searchB + " ") ||
				   searchA.endsWith(" " + searchB)){
					song.addArtist(artistManager.getById(artist));
					artistManager.getById(artist).addSong(song);
				}
			});
		});
		System.out.println("test č. 3: " + showStats());
		//TOD prejde všetkych znamych autorov a porovnava ich z nazvom súboru
		
		for(Song s : songManager.getValues()){
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
		array = artistManager.getArtistsWithSubtitles();
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
			artistManager.put(artist.subTitles[i], newArtists.get(i));
		}
		
		for(int i=0 ; i<songs.size() ; i++){
			songs.get(i).removeArtist(artist);//vymaže s pesničky stareho interpreta
			songs.get(i).addArtists(newArtists);//prida pesničkam novych interpretov
		}
		artistManager.remove(artist.getTitle());
	}
	
	public void changeSongArtist(Song s, String value) {
		String[] artists = value.split(";");
		System.out.println("song ma " + s.getArtistCount() + " autorov");
		System.out.println(s.getStringArtists() + " -> " + value);
		int counter = 0;
		for(String artist : artists){
			if(!s.hasArtist(artist)){
				Artist artistNew = artistManager.getByKeyName(artist);
				if(artistNew == null){
					artistNew = artistManager.getByKeyName(artist);
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
					System.out.println("odstanuje sa artist: " + a);
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
		System.out.println("song ma po uprave " + s.getArtistCount() + " autorov");
	}

}
