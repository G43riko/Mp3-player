import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.json.JSONObject;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jl.player.advanced.AdvancedPlayer;

public class MainMp3Library {
	static AdvancedPlayer player = null;;
	static File playing = null;
	Library lib = new Library();
	PlayerManager playerManager = new PlayerManager();
	
	public MainMp3Library(String path){
		createLibrary(FileSearcher.searchDir(path).stream().filter(a -> a.contains(".mp3")).collect(Collectors.toList()));
//		lib.fromJson(new JSONObject(FileManager.loadFromFile("data.txt")));
		
		String[] titles = new String[]{"#", "Artist", "Name", "Year", "Genre", "Length", "Bitrate", "Title", "Play"};

//		lib.postProcess();

		TopPanel topPanel = new TopPanel();
		TablePanel tablePanel = new TablePanel(playerManager, lib.getTableData(), titles, topPanel);
//		ArtistsPanel tablePanel = new ArtistsPanel(lib.getArtistData(), new String[]{"#", "Artist", "songs"}, topPanel);
		BottomMenu bottomPanel = new BottomMenu(playerManager);
		MenuPanel menuPanel = new MenuPanel(lib, tablePanel);
		topPanel.setSorter(tablePanel.getSorter());
		playerManager.setBottomPanel(bottomPanel);

		JFrame window = createWindow("MP3s library", 800, 600);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(menuPanel, BorderLayout.NORTH);
		panel.add(topPanel, BorderLayout.SOUTH);
		
		window.add(panel, BorderLayout.NORTH);
		window.add(tablePanel.getScrollPanel(), BorderLayout.CENTER);
		window.add(bottomPanel, BorderLayout.SOUTH);
		
		
//		FileManager.saveToFile("data.txt", lib.toJson().toString());
	}
	
	private void createLibrary(List<String> mp3s){
		int counter = 0;
		long start = System.currentTimeMillis();
		for(String fileName : mp3s){
			try {
				Mp3File mp3file = new Mp3File(fileName);
				Song song = lib.addSong(new File(fileName));
				song.setLength(mp3file.getLengthInSeconds());
				song.frames = (mp3file.getSampleRate()) * mp3file.getFrameCount();
				
				song.setBitrate(mp3file.getBitrate());
				if (mp3file.hasId3v1Tag()) {
					ID3v1 id3v1Tag = mp3file.getId3v1Tag();
					song.setGenre(id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
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
				//e.printStackTrace();
			}
		}
		System.out.println("trvalo to: " + (System.currentTimeMillis() - start));
	}
	
	private JFrame createWindow(String title, int width, int height){
		JFrame window = new JFrame(title);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width, height);
		window.setVisible(true);
		return window;
	}

	
	public static void main(String[] args) {
		MainMp3Library library = new MainMp3Library("/home/gabriel/Hudba");
	}

}
