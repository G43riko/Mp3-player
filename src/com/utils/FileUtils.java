package com.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.library.Artist;
import com.library.Library;
import com.library.Song;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class FileUtils {
	public static void createLibrary(Library lib, List<String> mp3s){
		int counter = 0;
		long start = System.currentTimeMillis();
		for(String fileName : mp3s){
			try {
				Mp3File mp3file = new Mp3File(fileName);
				Song song = lib.addSong(new File(fileName));
				//song.setLength(mp3file.getLengthInSeconds());
				song.setLength(mp3file.getLengthInMilliseconds());
				song.frames = (mp3file.getSampleRate()) * mp3file.getFrameCount();
				
				song.setBitrate(mp3file.getBitrate());
				if (mp3file.hasId3v1Tag()) {
					ID3v1 id3v1Tag = mp3file.getId3v1Tag();
					//song.setGenre(id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
					song.setGenre(id3v1Tag.getGenreDescription());
					song.setYear(id3v1Tag.getYear());
					
					String tagArtist = id3v1Tag.getArtist();
					if(!tagArtist.isEmpty()){
						Artist artist = lib.addArtist(tagArtist); 
						artist.addSong(song);
						song.addArtist(artist);
						song.setTagArtist(tagArtist);
					}
					
					String tagName = id3v1Tag.getTitle();
					if(!tagName.isEmpty()){
						song.setTagName(tagName);
					}
				}
				counter++;
				if(counter > 100){
					break;
				}
			} catch (UnsupportedTagException | InvalidDataException | IOException e) {
				System.out.println("chyba: " +fileName);
			}
		}
		System.out.println("trvalo to: " + (System.currentTimeMillis() - start));
	}
}
