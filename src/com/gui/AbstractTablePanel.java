package com.gui;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public interface AbstractTablePanel {
	public TableRowSorter<TableModel> getSorter();
//	public JScrollPane getScrollPanel();
	public void setValueData(Object[][] data);
}
