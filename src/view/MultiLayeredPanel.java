package view;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class MultiLayeredPanel extends JLayeredPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2885489630050927146L;
	private List<Container> panels;

	/**
	 * Create the panel.
	 */
	public MultiLayeredPanel(Container...panels) {
		setLayout(null);

		this.panels=new ArrayList<>(Arrays.asList(panels));
		addComponentListener(new ResizeListener());
		
		for(Container pnl:panels) {
			this.add(pnl);
		}
	}
	
	public void addLayers(Container...panels) {
		this.panels.addAll(Arrays.asList(panels));	
	}
	
	private class ResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent arg0) {
			resizeThings();
		}
		
		private void resizeThings() {
			int x=0;
			int y=0;
			for(Container pnl:panels) {
				pnl.setBounds(x,y, getWidth(), getHeight());
			}
			
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}
	}

}
