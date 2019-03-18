import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class Checkboxlistener extends JFrame {

    private JPanel jpAcc = new JPanel();
    private JList<String> checkBoxesJList;

    Checkboxlistener() {
        jpAcc.setLayout(new BorderLayout());
        String labels[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
        checkBoxesJList = new JList<String>(labels);

        checkBoxesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(checkBoxesJList);

        jpAcc.add(scrollPane);

        getContentPane().add(jpAcc);
        pack();
    }

    public static void main(String args[]) {
    	try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.getDefaults().put("ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {}));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Checkboxlistener cbl = new Checkboxlistener();
                cbl.setVisible(true);
            }
        });
    }
}