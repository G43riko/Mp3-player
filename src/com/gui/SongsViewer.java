package com.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ColorModel;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer.UIResource;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.library.Song;
import com.player.PlayerManager;

public class SongsViewer implements AbstractTablePanel{
//	private PlayerManager playerManager;
	private JTextField searchBar = new JTextField();
	private JScrollPane scrollPanel;
	private JPanel topPanel = new JPanel();
	private JPanel panel = new JPanel(new BorderLayout());
	private JTable table;
	TableRowSorter<TableModel> sorter;
	public final static Object[] titles = new String[]{"#", "Artist", "Name", "Year", "Genre", "Length", "Bitrate", "Title", "Play", "Source"};
	
	public void setValueData(Object[][] data) {
		DefaultTableModel model = new DefaultTableModel(data, titles); 
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
	
	public SongsViewer(Object[][] data, PlayerManager playerManager){
//		this.playerManager = playerManager;
//		AdvancedPlayer player = playerManager.getPlayer();
		
		table = new JTable(data, titles){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column){
				return column != 5 && column != 0 && column != 8 && column != 6;
			}
			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				super.getCellEditor(row, column).addCellEditorListener(new CellEditorListener() {					
					@Override
					public void editingStopped(ChangeEvent e) {
						Object val = getValueAt(table.getSelectedRow(), table.getSelectedColumn());
						if(val instanceof String){
							Song s = (Song)getValueAt(table.getSelectedRow(), 8);
							System.out.println("meni sa: " + s);
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
						if(column == 8){
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
						if(playerManager.isPlaying((Song)table.getValueAt(row, 8))){
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
				Song s = (Song)table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 8);
				playerManager.play(s);
	        	table.updateUI();
			}
		});
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
				if(entry.getValue(1) != null && entry.getValue(1).toString().toLowerCase().trim().contains(search))
					return true;
				if(entry.getValue(2) != null && entry.getValue(2).toString().toLowerCase().trim().contains(search))
					return true;
				if(entry.getValue(6) != null && entry.getValue(6).toString().toLowerCase().trim().contains(search))
					return true;
				return false;
			};
		};
	
		sorter.setRowFilter(filter);
		table.setRowSorter(sorter);
	}
	private void updateTable(){
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setMaxWidth(45);
		table.getColumnModel().getColumn(3).setMaxWidth(60);
		table.getColumnModel().getColumn(4).setMaxWidth(200);
		table.getColumnModel().getColumn(5).setMaxWidth(60);
		table.getColumnModel().getColumn(6).setMaxWidth(60);
		table.getColumnModel().getColumn(8).setMaxWidth(60);
		table.getColumnModel().getColumn(9).setMaxWidth(60);
	}
}
