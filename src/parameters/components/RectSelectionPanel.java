package parameters.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.opencv.core.Rect;

import view.MultiLayeredPanel;
import view.PanelFrame;

public class RectSelectionPanel extends MultiLayeredPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8659315087251458077L;
	private double[] rect;
	private JPanel pnlBack;
	private Function<double[], Rect> onRectChanged;
	private RectSelectionPanel instance=this;
	private JPanel rectPanel;
	private int nearestPoint=-1;
	private boolean dragging=false;

	
//	public static void main(String[] args) {
//		JPanel p=new JPanel();
//		p.setBackground(Color.BLACK);
//		RectSelectionPanel pnl=new RectSelectionPanel(p);
//		
//		JPanel n=new JPanel();
//		n.setLayout(new BorderLayout());
//		new PanelFrame(pnl).setVisible(true);
//	}
	
	/**
	 * @return the rect
	 */
	public Rect getRect() {
		int width=getWidth();
		int height=getHeight();
		
		Rect r=new Rect(new double[] {rect[0]*width,rect[1]*height,rect[2]*width,rect[3]*height});
		return r;
	}
	
	/**
	 * @return the rect
	 */
	public double[] getRect2() {
		return rect;
	}
	
	/**
	 * @param rect the rect to set
	 */
	public void setRect(double[] rect) {
		this.rect=rect;
	}
	
	/**
	 * @param rect the rect to set
	 */
	public void setRect(Rect rect) {
		setRect(rect,true);
	}
	
	/**
	 * @param rect the rect to set
	 */
	public void setRect(Rect rect, boolean triggerOnchange) {
		double width=getWidth();
		double height=getHeight();
		
		this.rect=new double[4];
		
		this.rect[0]=(double)rect.x/width;
		this.rect[1]=(double)rect.y/height;
		this.rect[2]=(double)rect.width/width;
		this.rect[3]=(double)rect.height/height;
		
		EventQueue.invokeLater(()->{
			revalidate();
			repaint();
		});
		if(onRectChanged!=null && triggerOnchange)
			onRectChanged.apply(getRect2());
	}
	
	/**
	 * @param onRectChanged the onRectChanged to set
	 */
	public void setOnRectChanged(Function<double[], Rect> onRectChanged) {
		this.onRectChanged = onRectChanged;
	}
	
	

	/**
	 * Create the panel.
	 */
	public RectSelectionPanel(JPanel pnlBackgrnd) {
		this.pnlBack=pnlBackgrnd;
		RectDragListener listener=new RectDragListener();
		rect=new double[] {0,0,1,1};
		rectPanel=new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 142342448923L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				
				Rect r=getRect();
				
				if(r!=null) {
					g2.setColor(Color.BLACK);
					g2.setStroke(new BasicStroke(4));
					g2.drawRect(r.x, r.y, r.width, r.height);
					g2.setColor(Color.WHITE);
					g2.setStroke(new BasicStroke(2));
					g2.drawRect(r.x, r.y, r.width, r.height);

					if(nearestPoint>=0) {
						int[] pnt=getInterestingPoints()[nearestPoint];
						g2.setColor(Color.BLACK);
						g2.fillOval(pnt[0]-6, pnt[1]-6, 12, 12);
						g2.setColor(dragging?Color.LIGHT_GRAY:Color.WHITE);
						g2.fillOval(pnt[0]-5, pnt[1]-5, 10, 10);
					}
				}else {
					rect=new double[] {0,0,1,1};
					repaint();
				}

			}
		};
		
		rectPanel.addMouseListener(listener);
		rectPanel.addMouseMotionListener(listener);
		rectPanel.setBorder(new LineBorder(Color.BLACK));
		this.addLayers(rectPanel);
		this.add(rectPanel);
		
		initialize();
	}
	
	protected void initialize() {
//		this.setLayout(new BorderLayout());
		if(pnlBack!=null) {
			this.add(pnlBack);
			this.addLayers(pnlBack);
		}
		
		rectPanel.setOpaque(false);
	}
	
	private int[][] getInterestingPoints(){
		Rect rect=getRect();
		int x=rect.x;
		int y=rect.y;
		int w=rect.width;
		int h=rect.height;
		
		int x2=x+w/2;
		int y2=y+h/2;
		
		int x3=x+w;
		int y3=y+h;

		
		int[][] ret=new int[][] {
			{x,y},	//NW 	0
			{x2,y},	//N		1
			{x3,y},	//NO	2
			{x3,y2},//O		3
			{x3,y3},//SO	4
			{x2,y3},//S		5
			{x,y3},	//SW	6
			{x,y2},	//W		7
			{x2,y2}	//CENTER8
		};
		
		return ret;
	}
	
	
	
	private class RectDragListener implements MouseListener, MouseMotionListener{
		private int prevX = -1;
		private int prevY = -1;
		private Rect oldRect;
		private boolean inside=false;

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			inside=true;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			inside=false;
			if(!dragging) {
				nearestPoint=-1;
			}
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}

		@Override
		public void mousePressed(MouseEvent e) {
			dragging=true;
			
			prevX = e.getXOnScreen();
			prevY = e.getYOnScreen();
			oldRect=getRect();
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			dragging=false;
			
			if(!inside) {
				nearestPoint=-1;
			}

			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(dragging) {
				
				int dx=e.getXOnScreen()-prevX;
				int dy=e.getYOnScreen()-prevY;
				
				int x=oldRect.x;
				int y=oldRect.y;
				int w=oldRect.width;
				int h=oldRect.height;
				switch(nearestPoint) {
					case 0:{
						x+=dx;
						y+=dy;
						w-=dx;
						h-=dy;
						break;
					}case 1:{ 	//N
						y+=dy;
						h-=dy;
						break;
					}case 2:{
						y+=dy;
						w+=dx;
						h-=dy;
						break;
					}case 3:{	//O
						w+=dx;
						break;
					}case 4:{
						w+=dx;
						h+=dy;
						break;
					}case 5:{	//S
						h+=dy;
						break;
					}case 6:{
						x+=dx;
						w-=dx;
						h+=dy;
						break;
					}case 7:{
						x+=dx;
						w-=dx;
						break;
					}case 8:{
						x+=dx;
						y+=dy;
						break;
					}
				}
				
				int width=getWidth();
				int height=getHeight();
				
				if(x<0) {
					x=0;
				}if(y<0) {
					y=0;
				}if(w<0) {
					w=0;
				}if(h<0) {
					h=0;
				}
				
				if(x>width-1) {
					x=width-1;
				}if(y>height-1) {
					y=height-1;
				}
				
				if(w<1) {
					w=1;
				}if(h<1) {
					h=1;
				}
				
				if(x+w>width) {
					w=width-x;
				}if(y+h>height) {
					h=height-y;
				}
				
				
				
				setRect(new Rect(new double[] {x,y,w,h}));
				if(onRectChanged!=null) {
					onRectChanged.apply(getRect2());
				}
				
				
				EventQueue.invokeLater(()->{
					revalidate();
					repaint();
				});
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if(!dragging) {
				nearestPoint=getIndexOfNearestInterestingPoint(e.getPoint());
				EventQueue.invokeLater(()->{
					revalidate();
					repaint();
				});
			}
		}
		
		//NW, N, NO,O,SO,S,SW,W,CENTER (Himmelsrichtungen)
		
		private int getIndexOfNearestInterestingPoint(Point pos) {
			int[][] interestingpnts=getInterestingPoints();
			int foundIndex=0;
			double distance=100000000;
			for(int i=0;i<interestingpnts.length;i++) {
				int[] p=interestingpnts[i];
				double dist=distanceBetweenPoints(pos,new Point(p[0],p[1]));
				if(dist<distance) {
					distance=dist;
					foundIndex=i;
				}
			}
			
			return foundIndex;
		}
		
		private double distanceBetweenPoints(Point p1, Point p2) {
			int dx=p1.x-p2.x;
			int dy=p1.y-p2.y;
			
			return Math.sqrt(dx*dx+dy*dy);
		}
	}
}
