package diagramming;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.ControlNode;
import com.mindfusion.diagramming.DiagramItem;
import com.mindfusion.diagramming.DiagramItemList;
import com.mindfusion.diagramming.DiagramLink;
import com.mindfusion.diagramming.DiagramLinkList;
import com.mindfusion.diagramming.DiagramNode;

import components.MatEditFunctionClassListCellRenderer;
import cvfunctions.MatEditFunction;
import diagramming.components.CVFunctionPanel;
import diagramming.components.StreamerPanel;
import main.MainFrame;
import main.MatSender;
import main.VideoStreamer;
import net.miginfocom.swing.MigLayout;
import view.MatReceiverPanel;
import view.PanelFrame;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class DiagramTestingFrame extends JFrame {

	private JPanel contentPane;
	private CustomDiagram customDiagram;
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnTest;
	private JLabel lblVideo;
	private JList<Class<? extends MatEditFunction>> fct_controlList;
	private JList<VideoStreamer> strmer_controlList;
	private CustomTransferHandler transferHandler;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
						UIManager.getDefaults().put("ScrollPane.ancestorInputMap",  
						        new UIDefaults.LazyInputMap(new Object[] {}));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					DiagramTestingFrame frame = new DiagramTestingFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DiagramTestingFrame() {
		transferHandler = new CustomTransferHandler();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][]", "[grow]"));
		
		customDiagram = new CustomDiagram();
		customDiagram.setBorder(new LineBorder(new Color(0, 0, 0)));
		customDiagram.setTransferHandler(transferHandler);
		Dimension d=new Dimension(50000,50000);
		customDiagram.setPreferredSize(d);
		
		scrollPane = new JScrollPane(customDiagram);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		contentPane.add(scrollPane, "cell 0 0,grow");
		
		customDiagram.setSize(10000,100000);
		
		customDiagram.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (CustomTransferHandler.isBoxCreated()) {
					dragNDropCreating(e);
				}
			}
		});
		
		panel = new JPanel();
		contentPane.add(panel, "cell 1 0,grow");
		panel.setLayout(new MigLayout("", "[35.00][]", "[grow]"));
		
		panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		lblVideo = new JLabel("Video");
		panel_1.add(lblVideo);
		
		btnTest = new JButton("Test");
		btnTest.addActionListener(new BtnActivateActionListener());
		panel.add(btnTest, "cell 1 0");
		
		// list box
		DefaultListModel<Class<? extends MatEditFunction>> fct_model = new DefaultListModel<>();
		for (Class<? extends MatEditFunction> fnctn : MainFrame.getMatEditFunctionClasses()) {
			fct_model.addElement(fnctn);
		}

		// list box
		DefaultListModel<VideoStreamer> strmer_model = new DefaultListModel<>();
		for (VideoStreamer streamer : MainFrame.getStreamers()) {
			strmer_model.addElement(streamer);
		}
		strmer_controlList = new JList<VideoStreamer>();
		panel_1.add(strmer_controlList);

		strmer_controlList.setModel(strmer_model);
		strmer_controlList.setDragEnabled(true);
		strmer_controlList.setTransferHandler(transferHandler);

		JLabel lblFunktionen = new JLabel("Funktionen");
		lblFunktionen.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(lblFunktionen);

		fct_controlList = new JList<Class<? extends MatEditFunction>>();
		panel_1.add(fct_controlList);

		fct_controlList.setModel(fct_model);
		fct_controlList.setCellRenderer(new MatEditFunctionClassListCellRenderer());
		fct_controlList.setDragEnabled(true);
		fct_controlList.setTransferHandler(transferHandler);
		
	}
	
	protected void dragNDropCreating(MouseEvent e) {
		Point pos = new Point(e.getX(), e.getY());

		JComponent component = new JButton(CustomTransferHandler.getText());

		ArrayList<VideoStreamer> streamers = (ArrayList<VideoStreamer>) MainFrame.getStreamers();

		Set<Class<? extends MatEditFunction>> fctClasses = MainFrame.getMatEditFunctionClasses();
		for (Class<? extends MatEditFunction> fctClass : fctClasses) {
			if (fct_controlList.getSelectedValue() != null && fct_controlList.getSelectedValue().equals(fctClass)) {
				component = new CVFunctionPanel(fctClass);
				fct_controlList.clearSelection();
				break;
			}
		}
		
		for (VideoStreamer streamer : MainFrame.getStreamers()) {
			if (strmer_controlList.getSelectedValue() != null
					&& strmer_controlList.getSelectedValue().equals(streamer)) {
				component = new StreamerPanel(streamer);
				strmer_controlList.clearSelection();
				break;
			}
		}
		
		CustomDiagramItem item = new CustomDiagramItem(component,customDiagram);
		if(component instanceof StreamerPanel) {
			item.getSelectedInput().setMaxConnectionNumber(0);
		}else {
			item.getSelectedInput().setMaxConnectionNumber(1);
		}

		customDiagram.addDiagramItem(item, pos);
		item.setSize(300, 220);

		CustomTransferHandler.cleanText();
		CustomTransferHandler.setBoxCreate(false);
		
		customDiagram.setVisible(false);
		customDiagram.setVisible(true);
	}
	
	private ArrayList<MatSender> createFunctions() {
		ArrayList<MatSender> ret=new ArrayList<>();
		
		List<CustomDiagramItem> diagramItems=customDiagram.getDiagramItems();
		
		for(CustomDiagramItem item:diagramItems) {
			MatSender sender=null;

			Component component=item.getComponent();
			
			if(component instanceof StreamerPanel) {
				StreamerPanel streamerPanel=(StreamerPanel)component;
				sender=streamerPanel.getMatSender();
			}else if(component instanceof CVFunctionPanel) {
				CVFunctionPanel panel=(CVFunctionPanel)component;
				sender=panel.getMatSender();
			}
			
			sender.clearMatReceiverFunctions();
			ret.add(sender);
			
			List<CustomDiagramItemConnection> links=item.getOutgoingConnections();
			for (CustomDiagramItemConnection link : links) {
				CustomDiagramItem linkNode=link.getTo().getDiagramItem();
				
				Component destComponent=linkNode.getComponent();
				if(destComponent instanceof CVFunctionPanel) {
					CVFunctionPanel destPanel=(CVFunctionPanel)destComponent;
					MatEditFunction destFunction=(MatEditFunction) destPanel.getMatSender();
					
					sender.addMatReceiver(destFunction);
				}
			}
		}
		
		return ret;
	}
	
	private class BtnActivateActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			ArrayList<MatSender> senders=createFunctions();
			
		}
	}
}
