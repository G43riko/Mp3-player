import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ArtistsPanel implements AbstractTablePanel{
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
	
	public ArtistsPanel(Object[][] data, Object[] titles, TopPanel topPanel){
		this.titles = titles;
		
		table = new JTable(data, titles){
			public boolean isCellEditable(int row, int column){
				return column != 0 && column != 2;
			}
		};
		table.setRowSelectionAllowed(true);
        table.setRowHeight(30);
        
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
				return false;
			};
		};
	
		sorter.setRowFilter(filter);
		table.setRowSorter(sorter);
	}
	public TableRowSorter<TableModel> getSorter() {
		return sorter;
	}
	public JScrollPane getScrollPanel(){
		return panel;
	}
	private void updateTable(){
		table.getColumnModel().getColumn(0).setMaxWidth(40);
		table.getColumnModel().getColumn(2).setMaxWidth(60);
	}
}
