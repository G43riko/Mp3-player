package com.gui;
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

import com.library.Song;
import com.player.PlayerManager;

public class BottomMenu extends Panel{
	private static final long serialVersionUID = 1L;
	Song actualSong = null;
	JLabel artist = new JLabel("ARTIST");
	JLabel name = new JLabel("NAME");
	JProgressBar progress = new JProgressBar(0, 100);
	PlayerManager player = null;
	
	JButton play = new JButton("\u25b6");
	
	int actualTime = 0;
	JButton pause = new JButton("\u23F8");
	
	JButton stop = new JButton("\u25fc");
	JButton next = new JButton("\u23ed");
	JButton prev = new JButton("\u23ee");
	
	Thread sliderThread = null;
	
	public void setSong(Song song){
		this.actualSong = song;
		actualTime = 0;
		artist.setText(song.getStringArtists());
		name.setText(song.getTagName());
		progress.setValue(actualTime);
		progress.setMaximum((int)song.getLength());
		progress.setString(Song.getFormattedTime(actualTime) + " / " + (actualSong == null ? 0 : actualSong.getLengthFormatted()));
	}
	@Override
	public Insets getInsets() {
		return new Insets(3, 3, 3, 3);
	}
	public BottomMenu(PlayerManager player) {
		this.player = player;
		stop.addActionListener(a -> player.stop());
		play.addActionListener(a -> player.play(null));
		updateData();
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
		progress.setString(actualTime +" / 0s");
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
		       progress.setString(Song.getFormattedTime(progressBarVal) + " /" + (actualSong == null ? 0 : actualSong.getLengthFormatted()));
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
	public void updateData() {
		play.setEnabled(!player.isPlaying() && player.getPlayingSong() != null);
		stop.setEnabled(player.isPlaying());
		
		if(sliderThread != null){
			sliderThread.interrupt();
			sliderThread = null;
		}
		if(player.isPlaying()){
			actualTime = 0;
			progress.setString(Song.getFormattedTime(actualTime) + " / " + (actualSong == null ? 0 : actualSong.getLengthFormatted()));
			sliderThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
						while(player.isPlaying()){
							progress.setString(Song.getFormattedTime(++actualTime) + " / " + (actualSong == null ? 0 : actualSong.getLengthFormatted()));
							progress.setValue(actualTime);
							Thread.sleep(1000);
						};
					} catch (InterruptedException e) {}
				}
			});
			sliderThread.start();
		}
		
	}
}
