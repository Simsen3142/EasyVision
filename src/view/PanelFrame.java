package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PanelFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Function<Void, Void> onWindowClosing;

	public PanelFrame(JPanel pnl, String title) {
		this(pnl);
		this.setTitle(title);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public PanelFrame(JPanel pnl) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = pnl;
		setContentPane(contentPane);
		
		this.addWindowListener(new ThisWindowListener());
	}
	
	public void setOnWindowClosing(Function<Void, Void> onWindowClosing) {
		this.onWindowClosing=onWindowClosing;
	}
	
	private class ThisWindowListener extends WindowAdapter implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void windowClosing(WindowEvent e) {
			if(onWindowClosing!=null) {
				onWindowClosing.apply(null);
			}
		}
	}
}
