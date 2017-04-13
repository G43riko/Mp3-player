package com.gui;

//0 - song Title
//1 - artist Title
//2 - artistSongs Title
public enum Pos {
	SONGS_ID(0, "#", 0, true),
	SONGS_ARTIST(0, "Artist", 1, true),
	SONGS_NAME(0, "Name", 2, true),
	SONGS_YEAR(0, "Year", 3, true),
	SONGS_GENRE(0, "Genre", 4, true),
	SONGS_LENGTH(0, "Length", 5, true),
	SONGS_BITRATE(0, "Bitrate", 6, true),
	SONGS_TITLE(0, "Title", 7, true),
	SONGS_SONG(0, "Song", 8, true),
	SONGS_SOURCE(0, "Source", 9, true),

	ARTISTS_ID(1, "#", 0, true),
	ARTISTS_NAME(1, "Name", 1, true),
	ARTISTS_SONGS(1, "Songs", 2, true),
	ARTISTS_INFO(1, "Info", 3, true),
	ARTISTS_SPOTIFY(1, "Spotify", 4, true),
	
	ARTIST_ID(2, "#", 0, true),
	ARTIST_NAME(2, "Name", 1, true),
	ARTIST_GENRE(2, "Genre", 2, true),
	ARTIST_LENGTH(2, "Length", 3, true),
	ARTIST_SONG(2, "Song", 4, true),
	ARTIST_SPOTIFY(2, "Spotify", 5, true);
	
    private final String label;
    private final int index;
    private final int type;
    private final boolean visible;
    
	private Pos(int type, String label, int index, boolean visible) {
		this.type = type;
		this.label = label;
		this.index = index;
		this.visible = visible;
	}
	
	//SONGS
	private static String getSongTitleById(int pos){
		for(Pos p : Pos.values()){
			if(p.index == pos && p.type == 0){
				return p.label;
			}
		}
		return "";
	}
	public static Object[] getSongTitles(){
		Object[] result = new Object[SongTitlesSize()];
		for(int i=0 ; i<result.length ; i++){
			result[i] = getSongTitleById(i);
		}
		return result;
	}
	public static int SongTitlesSize() {
		return titlesOfType(0);
	}
	
	
	
	//ARTIST
	private static String getArtistTitleById(int pos){
		for(Pos p : Pos.values()){
			if(p.index == pos && p.type == 2){
				return p.label;
			}
		}
		return "";
	}
	public static Object[] getArtistTitles(){
		Object[] result = new Object[ArtistTitlesSize()];
		for(int i=0 ; i<result.length ; i++){
			result[i] = getArtistTitleById(i);
//			System.out.println("artist title: " + result[i]);
		}
//		System.out.println("artist titles: " + result.length);
		return result;
	}
	public static int ArtistTitlesSize() {
//		System.out.println("artist size: " + titlesOfType(2));
		return titlesOfType(2);
	}
	
	
	//ARTISTS
	private static String getArtistsTitleById(int pos){
		for(Pos p : Pos.values()){
			if(p.index == pos && p.type == 1){
				return p.label;
			}
		}
		return "";
	}
	public static Object[] getArtistsTitles(){
		Object[] result = new Object[ArtistsTitlesSize()];
		for(int i=0 ; i<result.length ; i++){
			result[i] = getArtistsTitleById(i);
//			System.out.println("artists title: " + result[i]);
		}
//		System.out.println("artists titles: " + result.length);
		return result;
	}
	public static int ArtistsTitlesSize() {
//		System.out.println("artists size: " + titlesOfType(1));
		return titlesOfType(1);
	}

	
	//GLOBAL
	private static int titlesOfType(int type){
		int counter = 0;
		Pos[] data = Pos.values();
		for(int i=0 ; i<data.length ; i++){
			if(data[i].type == type){
				counter++;
			}
		}
		return counter;
	}
	public String getTitle(){
		return label;
	}
	public int getId(){
		return index;
	}
}
