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
	private JMenuItem menuItemSave = new JMenuItem("Save");
	private JMenuItem menuItemSaveAs = new JMenuItem("Save as");
	
	public MenuPanel(Library lib, AbstractTablePanel tablePanel){
		JMenu menu = new JMenu("File");
		menu.add(menuItemOpen);
		menuItemOpen.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("/home/gabriel/"));
			 int retrival = fileChooser.showSaveDialog(null);
		    if (retrival == JFileChooser.APPROVE_OPTION) {
		        lib.fromJson(new JSONObject(FileManager.loadFromFile(fileChooser.getSelectedFile())));
		        tablePanel.setValueData(lib.getTableData());
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
