package com.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.library.Library;
import com.player.PlayerManager;

public class GuiManager {
	private AbstractTablePanel songsViewer;
	private AbstractTablePanel artistsViewer;
	private Library lib;
	
	public void updateTable(){
		artistsViewer.setValueData(lib.getArtistData());
		songsViewer.setValueData(lib.getTableData());
	};
	
	public GuiManager(Library lib, PlayerManager playerManager){
		this.lib = lib;
		songsViewer = new SongsViewer(lib, playerManager);
		artistsViewer = new ArtistsViewer(lib.getArtistData(), playerManager);
		

		JTabbedPane tabbedPanel = new JTabbedPane();
		tabbedPanel.addTab("Songs", songsViewer.getPanel());
		tabbedPanel.addTab("Artists", artistsViewer.getPanel());
		
		BottomMenu bottomPanel = new BottomMenu(playerManager);
		MenuPanel menuPanel = new MenuPanel(lib, this);
		playerManager.setBottomPanel(bottomPanel);


		JFrame window = createWindow("MP3s library", 800, 600);	
		
		window.add(menuPanel, BorderLayout.NORTH);
		window.add(tabbedPanel, BorderLayout.CENTER);
		window.add(bottomPanel, BorderLayout.SOUTH);
		
		
		window.setVisible(true);
	}

	
	private JFrame createWindow(String title, int width, int height){
		JFrame window = new JFrame(title);
		
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width, height);
		

		window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		window.setUndecorated(true);
		window.setVisible(true);
		
		return window;
	}
}
