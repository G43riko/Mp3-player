package com.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.library.Song;

public class SongPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private GridBagConstraints c = new GridBagConstraints();
	private int CounterY = 0;
	
	public SongPanel(Song song) {
		super(new GridBagLayout());

		insert("Removed", song.isRemoved());
		insert("Best Name", song.getBestName());
		insert("Name", song.getName());
		insert("Tag Name", song.getTagName());
		insert("Artist", song.getStringArtists() + "(" + song.getArtistCount() + ")");
		insert("Title", song.getTitle());
		insert("Genre", song.getBestGenre());
		insert("Best Year", song.getBestYear());
		insert("Path", song.getAbsolutePath());
		insert("Length", song.getLengthFormatted());
		insert("Bitrate", song.getBitrate());
		
		insert("Spotify name", song.getSpotifyName());
		insert("Spotify duration", song.getSpotifyDuration());
		insert("Spotify id", song.getSpotifyId());

		insert("Tag name", song.getTagName());
		insert("Tag artist", song.getTagArtist());
	}
	
	private void insert(String key, Object value){
		add(new JLabel(key), getPos(0, CounterY));
		JTextField text = new JTextField(value == null ? "null" : value.toString());
		text.setEnabled(false);
		add(text, getPos(1, CounterY));
		CounterY++;
	}
	
	private GridBagConstraints getPos(int x, int y){
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		return c;
	}
}
