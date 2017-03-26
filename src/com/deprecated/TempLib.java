package com.deprecated;
import java.util.LinkedList;
import java.util.List;

public class TempLib {
	private List<String> tempArtists = new LinkedList<String>();
	private List<String> tempNames = new LinkedList<String>();
	
	public void addTempArtist(String title){
		tempArtists.add(title);
	}
	public void addTempName(String title){
		tempNames.add(title);
	}
	public List<String> getTempArtists() {
		return tempArtists;
	}
	public List<String> getTempNames() {
		return tempNames;
	}
}
