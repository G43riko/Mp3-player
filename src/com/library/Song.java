import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class Song {
	private String title;
	private List<Artist> artists = new LinkedList<Artist>();
	private String tagName = "";
	private String name = "";
	private long length = 0;
	private String tagArtist = "";
	private String year = "";
	private String genre = "";
	private String absoluePath = "";
	public int frames;
	private int bitrate;
	public File file; 
	
	public Song(JSONObject object){
		title = object.getString("title");
		if(object.has("tagName"))
			tagName = object.getString("tagName");
		length = object.getLong("length");
		if(object.has("tagArtist"))
			tagArtist = object.getString("tagArtist");
		if(object.has("year"))
			year = object.getString("year");
		if(object.has("genre"))
			genre = object.getString("genre");
		absoluePath = object.getString("absoluePath");
		bitrate = object.getInt("bitrate");
//		file = new File(absoluePath);
	}
	
	public Song(File file){
//		this.file = file;
		this.title = file.getName();
		this.absoluePath = file.getAbsolutePath();
	}
	public boolean containsArtists(Artist artist){
		return artists.contains(artist);
	}
	public void addArtist(Artist artist){
		if(!artists.contains(artist)){
			artists.add(artist);
		}
	}
	public void addArtists(List<Artist> newArtists){
		newArtists.stream().filter(a -> !artists.contains(a)).forEach(a -> artists.add(a));
		//artists.addAll(newArtists);
	}
	@Override
	public String toString() {
		return getStringArtists() + " - " + getTagName();
	}
	
	public JSONObject toJson(){
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		for(Artist artist : artists){
			array.put(artist.getTitle());
		}
		result.put("artists", array);
		result.put("title", title);
		result.put("tagName", tagName);
		result.put("length", length);
		result.put("tagArtist", tagArtist);
		result.put("absoluePath", absoluePath);
		result.put("bitrate", bitrate);
		result.put("year", year);
		result.put("genre", genre);
		return result;
	}
	public void removeArtist(Artist artist){
		artists.remove(artist);
	}
	public String getStringArtists() {return String.join("; ", artists.stream().map(a -> a.getTitle()).collect(Collectors.toList()));}
	public String getTagName() {return tagName;}
	public long getLength() {return length;}
	public long getBitrate() {return bitrate;}
	public String getYear() {return year;}
	public String getGenre() {return genre;}
	public String getTitle() {return title;}
	public String getTagArtist() {return tagArtist;}
	public String getAbsolutePath() {return absoluePath;}
	public int getArtistCount() {return artists.size();}

	public void setBitrate(int bitrate) {this.bitrate = bitrate;}
	public void setTagName(String name) {this.tagName = name;}
	public void setLength(long length) {this.length = length;}
	public void setYear(String year) {this.year = year;}
	public void setTagArtist(String tagArtist) {this.tagArtist = tagArtist;}
	public void setGenre(String genre) {this.genre = genre;}
	
}
