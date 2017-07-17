package com.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.API.spotify.SpotifyArtist;
import com.API.spotify.SpotifyData;
import com.API.spotify.SpotifySong;
import com.library.Artist;
import com.library.Library;
import com.library.Song;

public class Matcher {
	public static void JoinSpotifySongs(SpotifyData spotifyData, Library library){
		Map<String, SpotifyArtist> spotifyArtists = spotifyData.getArtists();
		System.out.println("songs: " + spotifyData.getSongs().size() + " == " + library.getSongsString().size());
		System.out.println("artists: " + spotifyArtists.size() + " == " + library.getNumberOfArtists());
		int equalAutors = 0;
		int equalAutorsWithEqualSongsNumber = 0;
		int equalAutorsWithEqualSongs = 0;
		int similarAutors = 0;
		Artist artist;
		for(SpotifyArtist a : spotifyArtists.values()){
			String artistName = a.getName().toLowerCase().replace("the ", "").trim();
			artist = library.getArtist(artistName);
			if(artist != null){
				equalAutors++;
				if(artist.getNumberOfSongs() <= a.getSongsSize()){
					equalAutorsWithEqualSongsNumber++;
					
					Set<String> songs = artist.getSongs().stream().map(e -> e.getBestName().toLowerCase().trim()).collect(Collectors.toSet());
					int equalSongs = a.songsContainsString(songs).size();
//					int equalSongs = 0;
//					for(SpotifySong s : a.songs){
//						if(songs.contains(s.name.toLowerCase().trim())){
//							equalSongs++;
//						}
//					}
					
					if(equalSongs >= artist.getNumberOfSongs()){
						equalAutorsWithEqualSongs++;
					}
					
				}
			}
			else if(library.getSimilarArtists(artistName).size() > 0){
				similarAutors++;
			}
		}
		
		ArrayList<SpotifySong> spotifySongs = spotifyData.getSongs();
		Set<Song> songs = library.getSongs();
		int equalSongs = 0;

		List<String> spotifySongsList = spotifySongs.parallelStream()
													.map(a -> a.name.toLowerCase()
																	.replace("radio edit", "")
																	.replace("original mix", "")
																	.replace("-", "")
																	.replaceAll("\\(.*\\)", "")
																	.replaceAll("feat.*$", "")
																	.trim())
													.distinct()
													.collect(Collectors.toList());
		
		Map<String, String> songsNames = spotifySongs.parallelStream()
													 .collect(Collectors.toMap(s -> s.name.toLowerCase()
																		  				  .replace(".mp3", "")
																		  				  .replaceAll("\\(.*\\)", "")
																		  				  .trim(), 
																		  	   s -> s.id, 
															  				   (a, b) -> a + ";" + b));
		
		for(String song : spotifySongsList){
			if(songsNames.containsKey(song)){
				equalSongs++;
			}
		}
		
//		equalSongs = (int)spotifySongsList.parallelStream().filter(a -> songNames.contains(a)).count();

		
		
		System.out.println("rovnakých autorov je: " + equalAutors);
		System.out.println("rovnakých autorov z rovnakym poctom pesniciek je: " + equalAutorsWithEqualSongsNumber);
		System.out.println("rovnakých autorov z rovnakymi pesnickami je: " + equalAutorsWithEqualSongs);
		System.out.println("podobných autorov je: " + similarAutors);
		
		System.out.println("rovnakých pesničiek je: " + equalSongs);
		
		//kolko je identických autorov
			//kolko je takých čo majú rovnaký počet pesničiek
			//kolko pesničiek ma rovnaky názov
		//kolko autorov v spotify je substring v autorovy u nás
		//nenajdenych našich autorov nieje v spotify autoroch
		
	}
}
