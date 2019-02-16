package diagramming;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import components.MatEditFunctionClassListCellRenderer;
import cvfunctions.MatEditFunction;
import database.OftenUsedObjects;
import database.Serializing;
import diagramming.components.MatEditFunctionDiagramPanel;
import diagramming.components.MatReceiverNSenderPanel;
import diagramming.components.StreamerPanel;
import main.MainFrame;
import main.MatReceiver;
import main.MatSender;
import net.miginfocom.swing.MigLayout;
import recording.VideoStreamer;
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

public class DiagramPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CustomDiagram customDiagram;
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnTest;
	private JLabel lblVideo;
	private JList<Class<? extends MatEditFunction>> fct_controlList;
	private JList<VideoStreamer> strmer_controlList;
	private CustomTransferHandler transferHandler;
	private JScrollPane scrollPane;
	private JButton btnSave;
	private JButton btnLoad;
	private JPanel panel_2;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					try {
//						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//						UIManager.getDefaults().put("ScrollPane.ancestorInputMap",  
//						        new UIDefaults.LazyInputMap(new Object[] {}));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//					DiagramPanel panel = new DiagramPanel();
//					new PanelFrame(panel).setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	public DiagramPanel() {
		super();
		transferHandler = new CustomTransferHandler();
		
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new MigLayout("", "[grow][]", "[grow]"));
		
		customDiagram = new CustomDiagram();
		customDiagram.setBorder(new LineBorder(new Color(0, 0, 0)));
		customDiagram.setTransferHandler(transferHandler);
		Dimension d=new Dimension(50000,50000);
		customDiagram.setPreferredSize(d);
		
		scrollPane = new JScrollPane(
				customDiagram
				);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		this.add(scrollPane, "cell 0 0,grow");
		
		customDiagram.setSize(10000,100000);
		
		customDiagram.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (CustomTransferHandler.isBoxCreated()) {
					createNAddMatSenderDiagramItem(new Point(e.getX(),e.getY()));
				}
			}
		});
		
		panel = new JPanel();
		this.add(panel, "cell 1 0,grow");
		panel.setLayout(new MigLayout("", "[35.00][]", "[grow]"));
		
		panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		lblVideo = new JLabel("Video");
		panel_1.add(lblVideo);
		
		panel_2 = new JPanel();
		panel.add(panel_2, "flowx,cell 1 0");
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		btnTest = new JButton("Test");
		panel_2.add(btnTest);
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new BtnSaveActionListener());
		panel_2.add(btnSave);
		
		btnLoad = new JButton("Load");
		btnLoad.addActionListener(new BtnLoadActionListener());
		panel_2.add(btnLoad);
		btnTest.addActionListener(new BtnActivateActionListener());
		
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
		
		customDiagram.addDiagramListener(new CustomDiagramListener() {
			@Override
			public void onDeleteItem(CustomDiagramItem diagramItem) {
				MatSender sender=null;

				Component component=diagramItem.getComponent();
				
				if(component instanceof StreamerPanel) {
					StreamerPanel streamerPanel=(StreamerPanel)component;
					VideoStreamer streamer=((VideoStreamer)streamerPanel.getMatSender());
					sender=streamerPanel.getMatSender();
				}else if(component instanceof MatEditFunctionDiagramPanel) {
					MatEditFunctionDiagramPanel panel=(MatEditFunctionDiagramPanel)component;
					sender=panel.getMatSender();
				}
				
				sender.stop();
				sender.clearMatReceiverFunctions();
			}
			
			@Override
			public void onDeleteConnection(CustomDiagramItemConnection diagramItemConnection) {
			}
		});
		
	}
	
	private MatEditFunction initFunction(Class<? extends MatEditFunction> functionClass) {
		MatEditFunction function=null;
		
		try {
			function=functionClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return function;
	}
	
	private void createNAddMatSenderDiagramItem(Point pos) {
		MatSender sender=null;
		Set<Class<? extends MatEditFunction>> fctClasses = MainFrame.getMatEditFunctionClasses();
		for (Class<? extends MatEditFunction> fctClass : fctClasses) {
			if (fct_controlList.getSelectedValue() != null && fct_controlList.getSelectedValue().equals(fctClass)) {
				sender=initFunction(fctClass);
				fct_controlList.clearSelection();
				break;
			}
		}
		
		for (VideoStreamer streamer : MainFrame.getStreamers()) {
			if (strmer_controlList.getSelectedValue() != null
					&& strmer_controlList.getSelectedValue().equals(streamer)) {
				sender=streamer;
				strmer_controlList.clearSelection();
				break;
			}
		}
		
		if(sender!=null) {
			addMatSenderDiagramItem(pos,sender);
		}
	}
	
	private CustomDiagramItem addMatSenderDiagramItem(Point pos, MatSender sender) {
		CustomDiagramItem item;
		MatReceiverNSenderPanel panel;
		if(sender instanceof VideoStreamer) {
			panel=new StreamerPanel((VideoStreamer) sender);
		}else if(sender instanceof MatEditFunction) {
			panel=new MatEditFunctionDiagramPanel((MatEditFunction)sender);
		}else {
			panel=new MatReceiverNSenderPanel(sender,"Name") {};
		}
		
		item = new CustomDiagramItem(panel,customDiagram);
		
		if(panel instanceof StreamerPanel) {
			item.getSelectedInput().setMaxConnectionNumber(0);
		}else {
			item.getSelectedInput().setMaxConnectionNumber(1);
			if(panel instanceof MatEditFunctionDiagramPanel) {
				MatEditFunction function=(MatEditFunction)((MatEditFunctionDiagramPanel)panel).getMatSender();
				if(!function.isSend()) {
					item.getSelectedOutput().setMaxConnectionNumber(0);
				}
			}
		}

		customDiagram.addDiagramItem(item, pos);
		item.setSize(300, 220);

		CustomTransferHandler.cleanText();
		CustomTransferHandler.setBoxCreate(false);
		
		customDiagram.setVisible(false);
		customDiagram.setVisible(true);
		
		return item;
	}
	
	private ArrayList<MatSender> createFunctions() {
		ArrayList<MatSender> ret=new ArrayList<>();
		
		List<CustomDiagramItem> diagramItems=customDiagram.getDiagramItems();
		
		for(CustomDiagramItem item:diagramItems) {
			MatSender matSender=null;

			Component component=item.getComponent();
			
			if(component instanceof StreamerPanel) {
				StreamerPanel streamerPanel=(StreamerPanel)component;
				VideoStreamer streamer=((VideoStreamer)streamerPanel.getMatSender());
				streamer.stop();
				streamer.start();
				matSender=streamerPanel.getMatSender();
			}else if(component instanceof MatEditFunctionDiagramPanel) {
				MatEditFunctionDiagramPanel panel=(MatEditFunctionDiagramPanel)component;
				matSender=panel.getMatSender();
				matSender.stop();
			}
			
			MatSender sender=(MatSender)matSender;
			sender.clearMatReceiverFunctions();
			ret.add(sender);
			
			List<CustomDiagramItemConnection> links=item.getOutgoingConnections();
			for (CustomDiagramItemConnection link : links) {
				CustomDiagramItem linkNode=link.getTo().getDiagramItem();
				
				MatReceiver receiver=null;
				Component destComponent=linkNode.getComponent();
				if(destComponent instanceof MatEditFunctionDiagramPanel) {
					MatEditFunctionDiagramPanel destPanel=(MatEditFunctionDiagramPanel)destComponent;
					MatEditFunction destFunction=(MatEditFunction) destPanel.getMatSender();
					
					receiver=destFunction;
				}
				
				sender.addMatReceiver(receiver);
			}
		}
		
		return ret;
	}
	
	private class BtnActivateActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			ArrayList<MatSender> senders=createFunctions();
			
		}
	}
	
	private class BtnSaveActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			createFunctions();
			
			ArrayList<MatSender> senders=new ArrayList<>();
			List<CustomDiagramItem> diagramItems=customDiagram.getDiagramItems();

			for(CustomDiagramItem item:diagramItems) {
				MatSender matSender=null;
				Component component=item.getComponent();
				
				if(item.getSelectedInput().getMaxConnectionNumber()<1) {
					if(component instanceof MatReceiverNSenderPanel) {
						MatReceiverNSenderPanel panel=(MatReceiverNSenderPanel)component;
						matSender=panel.getMatSender();
						senders.add(matSender);
					}
				}
			}
			
			System.out.println("SAVING");
			Serializing.serialize(senders, Serializing.showSaveDialog());
			System.out.println("DONE SAVING");
		} 
	}
	
	private void createDiagram(ArrayList<MatSender> senders) {
		int posX=5;
		int posY=5;
		
		for(MatSender sender:senders) {
			posY=handleDiagramMatCreationTree(posX,posY,sender,posY,null)+10;
		}
	}
	
	private int handleDiagramMatCreationTree(int posX, int posY, MatSender sender, int highestY, CustomDiagramItem ancestorItem) {
		Point p=new Point(posX,posY);
		CustomDiagramItem item=addMatSenderDiagramItem(p,sender);
		int ownY=posY+item.getHeight();
		highestY=ownY>highestY?ownY:highestY;

		if(ancestorItem!=null) {
			ancestorItem.connectTo(item);
		}
		
		int i=0;
		
		List<MatReceiver> receiversToRemove=new ArrayList<>();
		for(MatReceiver receiver:sender.getReceivers()) {
			if(receiver instanceof MatSender) {
				int y=handleDiagramMatCreationTree(posX+item.getWidth()+30,posY+(i*(30+item.getHeight())),(MatSender)receiver, highestY, item);
				highestY=y>highestY?y:highestY;
				i++;
			}else {
				receiversToRemove.add(receiver);
			}
		}
		sender.getReceivers().removeAll(receiversToRemove);
		
		System.out.println(highestY);
		return highestY;
	}
	
	private class BtnLoadActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			ArrayList<MatSender> senders;
			try {
				senders=(ArrayList<MatSender>)Serializing.deSerialize(Serializing.showOpenDialog());
			}catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			createDiagram(senders);
		}
	}
}
