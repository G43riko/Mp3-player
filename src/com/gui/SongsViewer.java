import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class TablePanel implements AbstractTablePanel{
	private PlayerManager playerManager;
	private JScrollPane panel;
	private JTable table;
	TableRowSorter<TableModel> sorter;
	private Object[] titles;
	
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
	public JScrollPane getScrollPanel(){
		return panel;
	}
	
	public TablePanel(PlayerManager playerManager, Object[][] data, Object[] titles, TopPanel topPanel){
		this.titles = titles;
		this.playerManager = playerManager;
		AdvancedPlayer player = playerManager.getPlayer();
		
		table = new JTable(data, titles){
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
				return new TableCellRenderer() {
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						if(value == null || value.toString().isEmpty()){
							return new JLabel("");
						}
						if(column == 8){
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
		table.setRowSelectionAllowed(true);
        table.setRowHeight(30);
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
		
		updateTable();
		panel = new JScrollPane(table);
		sorter = new TableRowSorter<TableModel>(table.getModel());
		
		
		sorter.setComparator(0, (a, b)->(Integer.parseInt(a.toString()) - Integer.parseInt(b.toString())));
		RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
			public boolean include(Entry entry) {
				String search = topPanel.getSearch().toLowerCase().trim();
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
		table.getColumnModel().getColumn(0).setMaxWidth(40);
		table.getColumnModel().getColumn(3).setMaxWidth(60);
		table.getColumnModel().getColumn(4).setMaxWidth(200);
		table.getColumnModel().getColumn(5).setMaxWidth(60);
		table.getColumnModel().getColumn(6).setMaxWidth(60);
		table.getColumnModel().getColumn(8).setMaxWidth(60);
	}
}
