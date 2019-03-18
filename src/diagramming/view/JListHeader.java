package diagramming.view;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.function.Function;

import javax.swing.JLabel;
import javax.swing.border.MatteBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JListHeader extends JPanel {
	private String text;
	private JLabel lblText;
	private InflationPanel lblInflate;
	private boolean inflated=true;
	private Function<Boolean, Void> onInflate;
	
	/**
	 * @param onInflate the onInflate to set
	 */
	public void setOnInflate(Function<Boolean, Void> onInflate) {
		this.onInflate = onInflate;
	}

	/**
	 * Create the panel.
	 */
	public JListHeader(String text) {
		addMouseListener(new ThisMouseListener());
		this.text = text;
		initialize();
	}

	public void initialize() {
		setLayout(new MigLayout("gap 0, insets 0 6 0", "[50%,grow][30%:n:30%]", "[36px:n:36px]"));

		lblText = new JLabel(text);
		add(lblText, "cell 0 0");
		setBorder(new MatteBorder(0, 0, 1, 0, (Color) getForeground()));
		setOpaque(false);
		setFont(new Font("Tahoma", Font.BOLD, 14));

		lblInflate = new InflationPanel();
//		setLblInflate();
		add(lblInflate, "cell 1 0,grow");
	}

	public void setFont(Font font) {
		if (lblText != null)
			lblText.setFont(font);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (lblText != null)
			lblText.setForeground(fg);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		int width = getWidth();
		int height = getHeight();
		int height2 = height / 2;

		g.fillRoundRect(0, 0, width, height, 10, 10);
		if(inflated) {
			g.fillRect(0, height2, width, height2 + 1);
			g.setColor(getForeground());
			g.drawLine(0, height-1, width, height-1);
		}else {
			setBorder(null);
		}

		super.paint(g);
	}
	
	private class InflationPanel extends JPanel{
		private Polygon inflationSign;
		
		public InflationPanel() {
			setOpaque(false);
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(getForeground());
			int y=getHeight()/2;
			drawArrowHead(g2, new Point(getWidth()/2,inflated?y:y+1));
		}
		
		private void drawArrowHead(Graphics2D g2d, Point p) {
			if (inflationSign == null) {
				inflationSign = new Polygon();
				inflationSign.addPoint(0, 2);
				inflationSign.addPoint(-10, -2);
				inflationSign.addPoint(10, -2);
			}
			g2d.translate(p.x - 2, p.y);
			if(inflated)
				g2d.rotate(Math.PI);
			g2d.fill(inflationSign);
		}
	}

	private class ThisMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent arg0) {
			inflated=!inflated;
			lblInflate.inflationSign=null;
			EventQueue.invokeLater(()->{
				getParent().revalidate();
				getParent().repaint();
			});
			if(onInflate!=null) {
				onInflate.apply(inflated);
			}
		}
	}
}
