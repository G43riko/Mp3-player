package com.gui;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.json.JSONObject;

import com.library.Library;
import com.utils.FileManager;


public class MenuPanel extends JMenuBar{
	private static final long serialVersionUID = 1L;
	
	private JMenuItem menuItemOpen = new JMenuItem("Open");
	private JMenuItem menuItemImportFile = new JMenuItem("Import from file");
	private JMenuItem menuItemImportDir = new JMenuItem("Import from directory");
	private JMenuItem menuItemSave = new JMenuItem("Save");
	private JMenuItem menuItemSaveAs = new JMenuItem("Save as");
	
	public MenuPanel(Library lib, GuiManager guiManager){
		JMenu menu = new JMenu("File");
		
		menu.add(menuItemImportFile);
		menuItemImportFile.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			int retrival = fileChooser.showOpenDialog(null);
		    if (retrival == JFileChooser.APPROVE_OPTION) {
		        lib.importFileFromFile(fileChooser.getSelectedFile().getAbsolutePath());
		        guiManager.updateTable();
		    }
		});
		
		menu.add(menuItemImportDir);
		menuItemImportDir.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setCurrentDirectory(new File("/home/gabriel/"));
			int retrival = fileChooser.showOpenDialog(null);
		    if (retrival == JFileChooser.APPROVE_OPTION) {
		    	lib.importFilesFromDirectory(fileChooser.getSelectedFile().getAbsolutePath());
		        guiManager.updateTable();
		    }
		});
		
		menu.add(menuItemOpen);
		menuItemOpen.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("/home/gabriel/"));
			int retrival = fileChooser.showSaveDialog(null);
		    if (retrival == JFileChooser.APPROVE_OPTION) {
		        lib.fromJson(new JSONObject(FileManager.loadFromFile(fileChooser.getSelectedFile())));
		        guiManager.updateTable();
//		        tablePanel.setValueData(lib.getTableData());
		    }
			
		});
		
		
		menu.add(menuItemSave);
		menu.add(menuItemSaveAs);
		menuItemSaveAs.addActionListener(e->{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("/home/gabriel/"));
		    int retrival = fileChooser.showSaveDialog(null);
		    if (retrival == JFileChooser.APPROVE_OPTION) {
		        FileManager.saveToFile(fileChooser.getSelectedFile(), lib.toJson().toString());
		    }
		});
		add(menu);
	}
}
