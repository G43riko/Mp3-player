package com.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ColorModel;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer.UIResource;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.library.Artist;
import com.library.Library;
import com.library.Song;
import com.player.PlayerManager;

public class SongsViewer implements AbstractTablePanel{
//	private PlayerManager playerManager;
	private JTextField searchBar = new JTextField();
	private JScrollPane scrollPanel;
	private JPanel topPanel = new JPanel();
	private JPanel panel = new JPanel(new BorderLayout());
	private JTable table;
	private Library library;
	TableRowSorter<TableModel> sorter;
//	public final static Object[] titles = new String[]{"#", "Artist", "Name", "Year", "Genre", "Length", "Bitrate", "Title", "Play", "Source"};
	
	public void setValueData(Object[][] data) {
		DefaultTableModel model = new DefaultTableModel(data, Pos.getSongTitles()); 
		table.setModel(model);
		sorter.setModel(model);
		updateTable();
		table.revalidate();
	}
	
	public TableRowSorter<TableModel> getSorter() {
		return sorter;
	}
	public JPanel getPanel(){
		return panel;
	}
	
	public SongsViewer(Library lib, PlayerManager playerManager){
		library = lib;
		Object[][] data = lib.getTableData();
//		this.playerManager = playerManager;
//		AdvancedPlayer player = playerManager.getPlayer();
		
		table = new JTable(data, Pos.getSongTitles()){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column){
				return column != Pos.SONGS_BITRATE.getId() && column != Pos.SONGS_ID.getId() && column != Pos.SONGS_SONG.getId() && column != Pos.SONGS_LENGTH.getId() && column != Pos.SONGS_SOURCE.getId();
			}
			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				super.getCellEditor(row, column).addCellEditorListener(new CellEditorListener() {					
					@Override
					public void editingStopped(ChangeEvent e) {
						Object val = getValueAt(table.getSelectedRow(), table.getSelectedColumn());
						if(val instanceof String){
							Song s = (Song)getValueAt(table.getSelectedRow(), Pos.SONGS_SONG.getId());
							s.setName((String)getValueAt(table.getSelectedRow(), Pos.SONGS_NAME.getId()));
							lib.changeSongArtist(s, (String)getValueAt(table.getSelectedRow(), Pos.SONGS_ARTIST.getId()));
						}
					}
					public void editingCanceled(ChangeEvent e) {}
				});
				return super.getCellEditor(row, column);
			}
			public TableCellRenderer getCellRenderer(int row, int column) {
				TableCellRenderer r = super.getCellRenderer(row, column);
				return new TableCellRenderer() {
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						Component defaultComponent = r.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						if(value == null || value.toString().isEmpty()){
							return defaultComponent;
//							return new JLabel("");
						}
						if(column == Pos.SONGS_SONG.getId()){
							Song song = (Song)value;
							JButton button = new JButton(playerManager.isPlaying(song) ? "\u25fc" : "\u25b6");
							button.setEnabled(!song.getAbsolutePath().isEmpty());
							return button;
						}
						JLabel label = new JLabel(value.toString());
						if(isSelected){
							label.setForeground(Color.blue);
							label.setBackground(Color.red);
							label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()+3));
						}
						if(playerManager.isPlaying((Song)table.getValueAt(row, Pos.SONGS_SONG.getId()))){
							defaultComponent.setFont(new Font(defaultComponent.getFont().getFontName(), Font.BOLD, defaultComponent.getFont().getSize()+3));
							defaultComponent.setForeground(Color.black);
						}
						return defaultComponent;
//						return label;
					}
				};
			};
		};
		
		table.setSelectionBackground(Color.PINK);
		table.setSelectionForeground(Color.gray);
		table.setRowSelectionAllowed(true);
        table.setRowHeight(30);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), "CtrlF");
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK), "CtrlJ");
        table.getActionMap().put("CtrlF", new AbstractAction() {
        	private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg) {
				searchBar.requestFocusInWindow();
			}
        });
        table.getActionMap().put("Enter", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg) {
				Song s = (Song)table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), Pos.SONGS_SONG.getId());
				playerManager.play(s);
	        	table.updateUI();
			}
		});
        table.getActionMap().put("CtrlJ", new AbstractAction() {
        	private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg) {
				processJoin(table.getSelectedRows());
			}
        });
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() != 3){
					return;
				}
				JMenuItem menuItem = null;
				JPopupMenu popup =  new JPopupMenu();
		        int row = e.getY() / table.getRowHeight(); 
				if(table.getSelectedRowCount() <= 1){
					table.setRowSelectionInterval(row, row);
				}
				
		        popup.add(new JMenuItem("Prehrať") );
		        if(table.getSelectedRowCount() >= 2){
		        	menuItem = new JMenuItem("Zlúčiť");
		        	menuItem.addActionListener(a -> processJoin(table.getSelectedRows()));
		        	popup.add(menuItem);
		        }
		        if(table.getSelectedRowCount() == 1){
			        menuItem = new JMenuItem("Detail");
			        menuItem.addActionListener(a -> showDetail(row));
			        popup.add(menuItem);
		        }
		        
		        menuItem = new JMenuItem("Zmazať");
		        menuItem.addActionListener(a -> deleteSong(table.getSelectedRows()));
		        popup.add(menuItem);
		        
				popup.show(table, e.getX(), e.getY());
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				int column = table.getColumnModel().getColumnIndexAtX(e.getX());
		        int row    = e.getY() / table.getRowHeight(); 
		        if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
		        	Object value = table.getValueAt(row, column);
		        	if(value instanceof Song){
		        		Song song =(Song)value;
		        		if(!song.getAbsolutePath().isEmpty()){
		        			playerManager.play(song);
		        		}
		        	};
		        	table.updateUI();
		        }
			}
		});
		
		
		updateTable();
		scrollPanel = new JScrollPane(table);
		
		
		sorter = new TableRowSorter<TableModel>(table.getModel());
		
		
		panel.add(scrollPanel, BorderLayout.CENTER);
		searchBar.setPreferredSize(new Dimension(200, 20));
		searchBar.addActionListener(a -> {
			sorter.modelStructureChanged();
			table.requestFocusInWindow();
		});
		
		topPanel.setPreferredSize(new Dimension(300, 30));
		topPanel.add(new JLabel("vyhladať: "));
		topPanel.add(searchBar);
		panel.add(topPanel, BorderLayout.NORTH);
		
		sorter.setComparator(0, (a, b)->(Integer.parseInt(a.toString()) - Integer.parseInt(b.toString())));
		RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
			public boolean include(Entry<?, ?> entry) {
				String search = searchBar.getText().toLowerCase().trim();
				if(search.isEmpty())
					return true;
				if(entry.getValue(Pos.SONGS_ARTIST.getId()) != null && entry.getValue(Pos.SONGS_ARTIST.getId()).toString().toLowerCase().trim().contains(search))
					return true;
				if(entry.getValue(Pos.SONGS_NAME.getId()) != null && entry.getValue(Pos.SONGS_NAME.getId()).toString().toLowerCase().trim().contains(search))
					return true;
				if(entry.getValue(Pos.SONGS_TITLE.getId()) != null && entry.getValue(Pos.SONGS_TITLE.getId()).toString().toLowerCase().trim().contains(search))
					return true;
				return false;
			};
		};
	
		sorter.setRowFilter(filter);
		table.setRowSorter(sorter);
	}
	
	private void showDetail(int selectedRows) {
		System.out.println("selectedRows: " + selectedRows);
		Song song = (Song)table.getValueAt(selectedRows, Pos.SONGS_SONG.getId());
		JDialog d2 = new JDialog((JFrame)SwingUtilities.getWindowAncestor(table), song.getBestName(), true);
		d2.setLocationRelativeTo(d2.getOwner());
		System.out.println("song: " + song);
  		d2.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  		d2.add(new SongPanel(song));
  		d2.setLocation(-300, -200);
  		d2.setSize(1000, 600);
  		d2.setVisible(true);
	}

	private void deleteSong(int[] selectedRows) {
		for(int i=0 ; i<selectedRows.length ; i++){
			((Song)table.getValueAt(selectedRows[i], Pos.SONGS_SONG.getId())).remove();;
		};
		setValueData(library.getTableData());
	}
	
	private void processJoin(int[] selectedRows){
		Song s1 = (Song)table.getValueAt(table.convertRowIndexToModel(selectedRows[0]), Pos.SONGS_SONG.getId());
		
		for(int i=1 ; i<selectedRows.length ; i++){
			Song.merge(s1, (Song)table.getValueAt(selectedRows[i], Pos.SONGS_SONG.getId()));
		};
		
		setValueData(library.getTableData());
	}
	
	private void updateTable(){
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(Pos.SONGS_ID.getId()).setMaxWidth(45);
		table.getColumnModel().getColumn(Pos.SONGS_YEAR.getId()).setMaxWidth(60);
		table.getColumnModel().getColumn(Pos.SONGS_GENRE.getId()).setMaxWidth(200);
		table.getColumnModel().getColumn(Pos.SONGS_LENGTH.getId()).setMaxWidth(60);
		table.getColumnModel().getColumn(Pos.SONGS_BITRATE.getId()).setMaxWidth(60);
		table.getColumnModel().getColumn(Pos.SONGS_SONG.getId()).setMaxWidth(60);
		table.getColumnModel().getColumn(Pos.SONGS_SOURCE.getId()).setMaxWidth(60);
	}
}
