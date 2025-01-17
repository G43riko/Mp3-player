package com.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.library.Artist;
import com.library.Song;
import com.player.PlayerManager;

public class ArtistPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private TableRowSorter<TableModel> sorter;

//	private Artist artist;
	
	
	public ArtistPanel(Artist artist, PlayerManager playerManager){
//		this.artist = artist;
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("Počet pesničiek: " + artist.getNumberOfSongs()));
		add(topPanel, BorderLayout.NORTH);
		Object[][] a = artist.getTableData();
		Object[] b = Pos.getArtistTitles();
		JTable table = new JTable(artist.getTableData(), Pos.getArtistTitles()){
			private static final long serialVersionUID = 1L;

			public TableCellRenderer getCellRenderer(int row, int column) {
				return new TableCellRenderer() {
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						if(value == null || value.toString().isEmpty()){
							return new JLabel("");
						}
						if(column == Pos.ARTIST_SONG.getId()){
							return new JButton(playerManager.isPlaying((Song)value) ? "\u25fc" : "\u25b6");
						}
						JLabel label = new JLabel(value.toString());
						if(isSelected){
							label.setForeground(Color.blue);
						}
						return label;
					}
				};
			};
		};
		sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		
		table.setRowSelectionAllowed(true);
        table.setRowHeight(30);
        
		table.getColumnModel().getColumn(Pos.ARTIST_ID.getId()).setMaxWidth(40);
		table.getColumnModel().getColumn(Pos.ARTIST_LENGTH.getId()).setMaxWidth(60);
		table.getColumnModel().getColumn(Pos.ARTIST_SONG.getId()).setMaxWidth(60);
		
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				int column = table.getColumnModel().getColumnIndexAtX(e.getX());
		        int row    = e.getY()/table.getRowHeight(); 
		        if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
		        	Object value = table.getValueAt(row, column);
		        	if(value instanceof Song){
		        		playerManager.play((Song)value);
		        	};
		        	table.updateUI();
		        }
			}
		});
		
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
}
