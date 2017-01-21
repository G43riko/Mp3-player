import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class BottomMenu extends Panel{
	Song actualSong = null;
	JLabel artist = new JLabel("ARTIST");
	JLabel name = new JLabel("NAME");
	JProgressBar progress = new JProgressBar(0, 100);
	PlayerManager player = null;
	
//	JLabel actualTime = new JLabel("0");
//	JLabel TotalTime = new JLabel("30");
	//JButton play = new JButton("Play");
	//JButton play = new JButton("\u25ba");
	JButton play = new JButton("\u25b6");
	
	int actualTime = 0;
//	JButton pause = new JButton("Pause");
	JButton pause = new JButton("\u23F8");
	
	//JButton stop = new JButton("Stop");
	JButton stop = new JButton("\u25fc");
	JButton next = new JButton("\u23ed");
	JButton prev = new JButton("\u23ee");
	
	public void setSong(Song song){
		this.actualSong = song;
		artist.setText(song.getStringArtists());
		name.setText(song.getTagName());
		actualTime = 0;
		progress.setValue(actualTime);
		progress.setMaximum((int)song.getLength());
		progress.setString(actualTime +"s /" + (actualSong == null ? 0 : actualSong.getLength()) + "s");
//		TotalTime.setText(Long.toString(song.getLength()));
	}
	@Override
	public Insets getInsets() {
		return new Insets(3, 3, 3, 3);
	}
	public BottomMenu(PlayerManager player) {
		this.player = player;
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(50,60));
		JPanel songPanel = new JPanel();
		
		songPanel.add(artist);
		songPanel.add(new JLabel(" - "));
		songPanel.add(name);
		
		JPanel controllPanel = new JPanel();
//		controllPanel.setPreferredSize(new Dimension(800, 600));
//		controllPanel.add(actualTime);
//		controllPanel.add(new JLabel("s/ "));
//		controllPanel.add(TotalTime);
//		controllPanel.add(new JLabel("s "));
		progress.setPreferredSize(new Dimension(100, 20));
		progress.setStringPainted(true);
		progress.setValue(progress.getMaximum());
		progress.setString(actualTime +"s / 0s");
		progress.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
		       int mouseX = e.getX();
		       int progressBarVal = (int)Math.round(((double)mouseX / (double)progress.getWidth()) * progress.getMaximum());
		       progress.setValue(progressBarVal);
		       progress.setString(progressBarVal +"s /" + (actualSong == null ? 0 : actualSong.getLength()) + "s");
		       player.playSkipTo(((float)progressBarVal / (float)progress.getMaximum()) * actualSong.frames);
			}
		});
		
		controllPanel.add(prev );
		controllPanel.add(play );
		controllPanel.add(pause);
		controllPanel.add(stop);
		controllPanel.add(next);
		
		add(songPanel, BorderLayout.WEST);
		add(controllPanel, BorderLayout.EAST);
		add(progress, BorderLayout.SOUTH);
	}
}
