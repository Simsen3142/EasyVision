package diagramming;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import components.ClassListCellRenderer;
import database.Serializing;
import diagramming.components.MatEditFunctionDiagramPanel;
import diagramming.components.MatReceiverNSenderPanel;
import diagramming.components.ParameterReceiverPanel;
import diagramming.components.StreamerPanel;
import functions.matedit.MatEditFunction;
import functions.streamer.VideoStreamer;
import main.MainFrame;
import main.MatMapReceiver;
import main.MatReceiver;
import main.MatSender;
import main.ParameterReceiver;
import net.miginfocom.swing.MigLayout;
import parameters.ParameterizedObject;

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
import java.awt.EventQueue;

import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public class DiagramPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CustomDiagram customDiagram;
	private JPanel panel;
	private JPanel pnlLists;
	private JButton btnTest;
	private CustomTransferHandler transferHandler;
	private JScrollPane scrollPane;
	private JButton btnSave;
	private JButton btnLoad;
	private JPanel pnlControl;
	private List<JList<?>> lists=new ArrayList<>();

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
					createNAddDiagramItem(new Point(e.getX(),e.getY()));
				}
			}
		});
		
		panel = new JPanel();
		this.add(panel, "cell 1 0,grow");
		panel.setLayout(new MigLayout("", "[35.00][]", "[grow]"));
		
		pnlLists = new JPanel();
		panel.add(pnlLists, "cell 0 0,grow");
		pnlLists.setLayout(new BoxLayout(pnlLists, BoxLayout.Y_AXIS));
		
		pnlControl = new JPanel();
		panel.add(pnlControl, "flowx,cell 1 0");
		pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.Y_AXIS));
		
		btnTest = new JButton("Test");
		pnlControl.add(btnTest);
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new BtnSaveActionListener());
		pnlControl.add(btnSave);
		
		btnLoad = new JButton("Load");
		btnLoad.addActionListener(new BtnLoadActionListener());
		pnlControl.add(btnLoad);
		btnTest.addActionListener(new BtnActivateActionListener());
		
		initLists();
		
		customDiagram.addDiagramListener(new CustomDiagramListener() {
			@Override
			public void onDeleteItem(CustomDiagramItem diagramItem) {
				Component component=diagramItem.getComponent();
				MatSender sender=null;
				ParameterReceiver rec=null;
				
				if(component instanceof MatReceiverNSenderPanel) {
					if(component instanceof StreamerPanel) {
						StreamerPanel streamerPanel=(StreamerPanel)component;
						sender=streamerPanel.getFunction();
					}else if(component instanceof MatEditFunctionDiagramPanel) {
						MatEditFunctionDiagramPanel panel=(MatEditFunctionDiagramPanel)component;
						sender=panel.getFunction();
					}
					
					sender.stop();
					sender.clearMatReceiverFunctions();
				}else if(component instanceof ParameterReceiverPanel) {
					rec=((ParameterReceiverPanel)component).getParamReceiver();
				}
				
				for(DiagramInput input:diagramItem.getInputs()) {
					for(CustomDiagramItemConnection con:input.getConnections()) {
						CustomDiagramItem fromItem=con.getFrom().getDiagramItem();
						Component fromComp=fromItem.getComponent();
						
						if(fromComp instanceof MatReceiverNSenderPanel) {
							MatSender fromSender=((MatReceiverNSenderPanel)fromComp).getFunction();
							if(sender!=null && sender instanceof MatReceiver) {
								fromSender.removeMatReceiver((MatReceiver)sender);
							}
							if(sender!=null && sender instanceof MatMapReceiver) {
								fromSender.removeMatMapReceiver((MatMapReceiver)sender);
							}
							if(rec!=null && rec instanceof ParameterReceiver) {
								fromSender.removeParamterReceiver(rec);
							}
						}
					}
				}
			}
			
			@Override
			public void onDeleteConnection(CustomDiagramItemConnection connection) {
			}

			@Override
			public void onCreateConnection(CustomDiagramItemConnection connection) {
				CustomDiagramItem to=connection.getTo().getDiagramItem();
				if(to.getComponent() instanceof ParameterReceiverPanel) {
					DiagramOutput from=connection.getFrom();
					if(!from.getName().equals("parameter_out")) {
						connection.setFrom((DiagramOutput)from.getDiagramItem().getConnector("parameter_out"));
					}
					
					EventQueue.invokeLater(()->{
						connection.setForeground(Color.BLUE);
						connection.revalidate();
						connection.repaint();
					});
				}
			}

			@Override
			public boolean onConnectionAvailable(CustomDiagramItem from, CustomDiagramItem to) {
				return true;
			}

			@Override
			public void onCreateItem(CustomDiagramItem diagramItem) {
			}

		});
	}
	
	private void addList(String name, Collection<?> items, ListCellRenderer<Object> cellRenderer) {
		JPanel pnl=new JPanel();
		pnl.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnl.setLayout(new BoxLayout(pnl,BoxLayout.Y_AXIS));
		
		JLabel lbl=new JLabel(name);
		pnl.add(lbl);
		
		DefaultListModel<Object> listModel=new DefaultListModel<>();
		for(Object item:items) {
			listModel.addElement(item);
		}
		
		JList<Object> controlList=new JList<>();
		pnl.add(controlList);
		controlList.setModel(listModel);
		
		if(cellRenderer!=null)
			controlList.setCellRenderer(cellRenderer);
		controlList.setDragEnabled(true);
		controlList.setTransferHandler(transferHandler);
		pnlLists.add(pnl);
		
		controlList.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		lists.add(controlList);
	}
	
	private void initLists() {
		addList("Streamers",MainFrame.getStreamers(),null);
		addList("Cv-Functions",MainFrame.getMatEditFunctionClasses(),new ClassListCellRenderer());
		addList("Param-Functions",MainFrame.getParameterReceiverClasses(),new ClassListCellRenderer());
	}
	
	private Object initFunction(Class<?> functionClass) {
		Object function=null;
		
		try {
			function=functionClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return function;
	}
	
	private void createNAddDiagramItem(Point pos) {
		MatSender sender=null;
		ParameterReceiver paramRec=null;
		
		for(JList<?> list:lists) {
			if (list.getSelectedValue() != null) {
				Object o=list.getSelectedValue();
				
				if(o instanceof Class<?>) {
					Set<Class<? extends MatEditFunction>> fctcvClasses = MainFrame.getMatEditFunctionClasses();
					for(Class<? extends MatEditFunction> fctClass:fctcvClasses) {
						if(fctClass.equals(o)) {
							sender=(MatEditFunction)initFunction(fctClass);
							break;
						}
					}
					
					if(sender==null) {
						Set<Class<? extends ParameterReceiver>> fctparamClasses = MainFrame.getParameterReceiverClasses();
						for(Class<? extends ParameterReceiver> fctClass:fctparamClasses) {
							if(fctClass.equals(o)) {
								paramRec=(ParameterReceiver)initFunction(fctClass);
								break;
							}
						}
					}
				}else if(o instanceof VideoStreamer) {
					for (VideoStreamer streamer : MainFrame.getStreamers()) {
						if (o.equals(streamer)) {
							sender=streamer;
							break;
						}
					}
				}
				
				list.clearSelection();
			}
		}
		
		
		if(sender!=null) {
			addMatSenderDiagramItem(pos,sender);
		}else if(paramRec!=null) {
			addParamReceiverDiagramItem(pos,paramRec);
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
				MatEditFunction function=(MatEditFunction)((MatEditFunctionDiagramPanel)panel).getFunction();
				if(!function.isSend()) {
					item.getSelectedOutput().setMaxConnectionNumber(0);
				}
			}
		}

		customDiagram.addDiagramItem(item, pos);
		item.setSize(300, 220);
		
		item.addDiagramConnector(new DiagramOutput(item, (nix)-> {
			int x=item.getX()+item.getWidth();
			int y=item.getY()+item.getHeight()/2+10;
			return new Point(x,y);
		}, "matmap_out"));
		
		item.addDiagramConnector(new DiagramOutput(item, (nix)-> {
			int x=item.getX()+item.getWidth();
			int y=item.getY()+item.getHeight()/2+20;
			return new Point(x,y);
		}, "parameter_out"));
		
		if(sender instanceof MatMapReceiver) {
			item.addDiagramConnector(new DiagramInput(item, (nix)-> {
				int x=item.getX();
				int y=item.getY()+item.getHeight()/2+10;
				return new Point(x,y);
			}, "matmap_in"));
		}
		if(sender instanceof ParameterReceiver) {
			item.addDiagramConnector(new DiagramInput(item, (nix)-> {
				int x=item.getX();
				int y=item.getY()+item.getHeight()/2+20;
				return new Point(x,y);
			}, "parameter_in"));
		}
		
		CustomTransferHandler.cleanText();
		CustomTransferHandler.setBoxCreate(false);
		
		EventQueue.invokeLater(()->{
			customDiagram.revalidate();
			customDiagram.repaint();
		});
		
		return item;
	}
	
	private CustomDiagramItem addParamReceiverDiagramItem(Point pos, ParameterReceiver receiver) {
		CustomDiagramItem item;
		JComponent panel;
		
		panel=new ParameterReceiverPanel(receiver, receiver.toString());//new MatReceiverNSenderPanel(receiver,"Name") {};
		
		item = new CustomDiagramItem(panel,customDiagram);
		
		item.getSelectedInput().setMaxConnectionNumber(1);
		item.getSelectedOutput().setMaxConnectionNumber(0);

		customDiagram.addDiagramItem(item, pos);
		item.setSize(300, 220);

		CustomTransferHandler.cleanText();
		CustomTransferHandler.setBoxCreate(false);
		
		EventQueue.invokeLater(()->{
			customDiagram.revalidate();
			customDiagram.repaint();
		});
		
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
				VideoStreamer streamer=((VideoStreamer)streamerPanel.getFunction());
				streamer.stop();
				streamer.start();
				matSender=streamerPanel.getFunction();
			}else if(component instanceof MatEditFunctionDiagramPanel) {
				MatEditFunctionDiagramPanel panel=(MatEditFunctionDiagramPanel)component;
				matSender=panel.getFunction();
				matSender.stop();
			}else {
				continue;
			}
			
			if(matSender!=null) {
				matSender.clearMatReceiverFunctions();

				List<CustomDiagramItemConnection> links=item.getOutgoingConnections();
				for (CustomDiagramItemConnection link : links) {
					CustomDiagramItem itemTo=link.getTo().getDiagramItem();
					
					
					Component destComponent=itemTo.getComponent();
					if(destComponent instanceof MatEditFunctionDiagramPanel) {
						MatReceiver matReceiver=null;

						MatEditFunctionDiagramPanel destPanel=(MatEditFunctionDiagramPanel)destComponent;
						MatEditFunction destFunction=(MatEditFunction) destPanel.getFunction();
						
						matReceiver=destFunction;
						
						matSender.addMatReceiver(matReceiver);
						
						//TODO: for other MatReceivers
					}else if(destComponent instanceof ParameterReceiverPanel) {
						ParameterReceiver paramReceiver=null;
						
						ParameterReceiverPanel destPanel=(ParameterReceiverPanel)destComponent;
						paramReceiver=destPanel.getParamReceiver();
						
						matSender.addParameterReceiver(paramReceiver);
					}
					
				}
				ret.add(matSender);
			}
			
		}
		
		return ret;
	}
	
	private class BtnActivateActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			ArrayList<MatSender> senders=createFunctions();
			
		}
	}
	
	public List<MatSender> getMatSenders(){
		List<MatSender> ret=new ArrayList<>();
		List<CustomDiagramItem> diagramItems=customDiagram.getDiagramItems();

		for(CustomDiagramItem item:diagramItems) {
			MatSender matSender=null;
			Component component=item.getComponent();

			if(component instanceof MatReceiverNSenderPanel) {
				MatReceiverNSenderPanel panel=(MatReceiverNSenderPanel)component;
				matSender=panel.getFunction();
				ret.add(matSender);
			}
		}
		
		return ret;
	}
	
	public void save(File file) {
		if(file!=null) {
			createFunctions();
			
			ArrayList<MatSender> senders=new ArrayList<>();
			List<CustomDiagramItem> diagramItems=customDiagram.getDiagramItems();
	
			for(CustomDiagramItem item:diagramItems) {
				MatSender matSender=null;
				Component component=item.getComponent();
				
				if(item.getSelectedInput().getConnections().size()<1) {
					if(component instanceof MatReceiverNSenderPanel) {
						MatReceiverNSenderPanel panel=(MatReceiverNSenderPanel)component;
						matSender=panel.getFunction();
						senders.add(matSender);
					}
				}
			}
			
			Serializing.serialize(senders, file);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void load(File file) {
		if(file!=null) {
			customDiagram.clear();
	
			ArrayList<MatSender> senders;
			
			try {
				senders=(ArrayList<MatSender>)Serializing.deSerialize(file);
			}catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			if(senders!=null) {
				createDiagram(senders);
			}
		}
	}
	
	private class BtnSaveActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			save(Serializing.showSaveDialog());
		} 
	}
	
	private void createDiagram(ArrayList<MatSender> senders) {
		int posX=5;
		int posY=5;
		
		for(MatSender sender:senders) {
			posY=handleDiagramCreationTree(posX,posY,sender,posY,null)+10;
		}
	}
	
	private int handleDiagramCreationTree(int posX, int posY, Object diagramObject, int highestY, CustomDiagramItem ancestorItem) {
		MatSender sender=null;
		ParameterReceiver paramReceiver=null;
		if(diagramObject instanceof MatSender) {
			sender=(MatSender)diagramObject;
		}
		if(diagramObject instanceof ParameterReceiver) {
			paramReceiver=(ParameterReceiver)diagramObject;
		}
		
		Point p=new Point(posX,posY);
		CustomDiagramItem item=null;
		
		if(sender!=null) {
			item=addMatSenderDiagramItem(p,sender);
		}else if(paramReceiver!=null) {
			item=addParamReceiverDiagramItem(p, paramReceiver);
		}

		int ownY=posY+item.getHeight();
		highestY=ownY>highestY?ownY:highestY;

		if(ancestorItem!=null) {
			ancestorItem.connectTo(item);
		}
		
		int i=0;
		
		if(sender!=null) {
			List<MatReceiver> receiversToRemove=new ArrayList<>();
			for(MatReceiver receiver:sender.getReceivers()) {
				if(receiver instanceof MatSender) {
					int y=handleDiagramCreationTree(posX+item.getWidth()+30,posY+(i*(30+item.getHeight())),(MatSender)receiver, highestY, item);
					highestY=y>highestY?y:highestY;
					i++;
				}else {
					receiversToRemove.add(receiver);
				}
			}
			for(ParameterReceiver receiver:sender.getParamReceivers()) {
				int y=handleDiagramCreationTree(posX+item.getWidth()+30,posY+(i*(30+item.getHeight())),(ParameterReceiver)receiver, highestY, item);
				highestY=y>highestY?y:highestY;
				i++;
			}
			sender.getReceivers().removeAll(receiversToRemove);
		}
		return highestY;
	}
	
	
	private class BtnLoadActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			load(Serializing.showOpenDialog());
		}
	}
}
