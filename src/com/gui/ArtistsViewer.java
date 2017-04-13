package com.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.library.Artist;
import com.player.PlayerManager;

public class ArtistsViewer implements AbstractTablePanel{
	private JScrollPane scrollPanel;
	private JTextField searchBar = new JTextField();
	private JPanel topPanel = new JPanel();
	private JPanel panel = new JPanel(new BorderLayout());
	private JTable table;
	private TableRowSorter<TableModel> sorter;
//	private Object[] titles = new String[]{"#", "Artist", "songs", "Info", "Spotify"};
	
	public void setValueData(Object[][] data) {
		DefaultTableModel model = new DefaultTableModel(data, Pos.getArtistsTitles()); 
		table.setModel(model);
		sorter.setModel(model);
		updateTable();
		table.revalidate();
	}
	
	public ArtistsViewer(Object[][] data, PlayerManager playerManager){
		
		table = new JTable(data, Pos.getArtistsTitles()){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column){
				return column == Pos.ARTISTS_NAME.getId();
			}
			public TableCellRenderer getCellRenderer(int row, int column) {
				return new TableCellRenderer() {
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						if(value == null || value.toString() == null || value.toString().isEmpty()){
							return new JLabel("");
						}
						if(column == Pos.ARTISTS_INFO.getId()){
							return new JButton("Info");
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
		        	if(value instanceof Artist){
		        		JDialog d2 = new JDialog((JFrame)SwingUtilities.getWindowAncestor(table), value.toString(), true);
		                d2.setLocationRelativeTo(d2.getOwner());
		        		d2.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		        		d2.add(new ArtistPanel((Artist)value, playerManager));
		        		d2.setLocation(-300, -200);
		        		d2.setSize(600, 400);
		        		d2.setVisible(true);
		        	};
		        	table.updateUI();
		        }
			}
		});
		table.setRowSelectionAllowed(true);
        table.setRowHeight(30);
        
        updateTable();
        
        scrollPanel = new JScrollPane(table);
        
        
        panel.add(scrollPanel, BorderLayout.CENTER);
        searchBar.setPreferredSize(new Dimension(200, 20));
		searchBar.addActionListener(a -> {
//			JTextField res = (JTextField)a.getSource();
			sorter.modelStructureChanged();
		});
		
		
		topPanel.setPreferredSize(new Dimension(300, 30));
		topPanel.add(new JLabel("vyhladať: "));
		topPanel.add(searchBar);
		panel.add(topPanel, BorderLayout.NORTH);
        
		sorter = new TableRowSorter<TableModel>(table.getModel());
		sorter.setComparator(Pos.ARTISTS_SONGS.getId(), (a, b)->(Integer.parseInt(a.toString()) - Integer.parseInt(b.toString())));
		RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
			public boolean include(Entry<?, ?> entry) {
				String search = searchBar.getText().toLowerCase().trim();
				if(search.isEmpty()){
					return true;
				}
				if(entry.getValue(1) != null && entry.getValue(1).toString().toLowerCase().trim().contains(search)){
					return true;
				}
				return false;
			};
		};
		table.setAutoCreateRowSorter(false);
		sorter.setRowFilter(filter);
		table.setRowSorter(sorter);
	}
	public TableRowSorter<TableModel> getSorter() {
		return sorter;
	}
	public JPanel getPanel(){
		return panel;
	}
	private void updateTable(){
		table.getColumnModel().getColumn(Pos.ARTISTS_ID.getId()).setMaxWidth(40);
		table.getColumnModel().getColumn(Pos.ARTISTS_SONGS.getId()).setMaxWidth(60);
		table.getColumnModel().getColumn(Pos.ARTISTS_INFO.getId()).setMaxWidth(80);
		table.getColumnModel().getColumn(Pos.ARTISTS_SPOTIFY.getId()).setMaxWidth(80);
	}
}
