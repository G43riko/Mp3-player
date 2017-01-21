import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;

public class TopPanel extends JPanel{
	private JTextField searchBar = new JTextField();
	
	public String getSearch(){
		return searchBar.getText();
	};
	public TopPanel() {
		setPreferredSize(new Dimension(300, 30));
		add(new JLabel("vyhladať: "));
		add(searchBar);


		searchBar.setPreferredSize(new Dimension(200, 20));
	}
	
	public void setSorter(TableRowSorter<?> sorter){
		searchBar.addActionListener(a -> {
			JTextField res = (JTextField)a.getSource();
			sorter.modelStructureChanged();
			
		});
	}
}
