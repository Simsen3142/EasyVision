package diagramming;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import components.ClassListCellRenderer;
import components.PictureButton;
import database.ImageHandler;
import database.Serializing;
import diagramming.components.FunctionPanel;
import diagramming.components.MatEditFunctionDiagramPanel;
import diagramming.components.MatReceiverNSenderPanel;
import diagramming.components.MultiMatEditFunctionDiagramPanel;
import diagramming.components.ParameterReceiverPanel;
import diagramming.components.StreamerPanel;
import diagramming.view.JListHeader;
import functions.Startable;
import functions.UniqueFunction;
import functions.matedit.MatEditFunction;
import functions.matedit.Switch;
import functions.matedit.multi.MultiMatEditFunction;
import functions.parameterreceiver.RobotControl;
import functions.streamer.MatStreamer;
import main.MainFrame;
import main.MatMapReceiver;
import main.MatReceiver;
import main.MatSender;
import main.ParameterReceiver;
import net.miginfocom.swing.MigLayout;
import parameters.ParameterizedObject;

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
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;

public class DiagramPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Diagram customDiagram;
	private JPanel pnlLists;
	private JButton btnStart;
	private CustomTransferHandler transferHandler;
	private JScrollPane scrollPane;
	private JPanel pnlControl;
	private List<JList<?>> lists = new ArrayList<>();
	private boolean loading = false;
	private JButton btnStop;
	private Startable selectedStartable=null;
	private Dimension itemSize=new Dimension(300, 220);

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
		setBackground(new Color(250,250,255));
		
		transferHandler = new CustomTransferHandler();

		this.setLayout(new MigLayout("insets 0, gap 0", "[80%][grow]", "[][grow]"));

		customDiagram = new Diagram();
		customDiagram.setBorder(new LineBorder(new Color(0, 0, 0)));
		customDiagram.setTransferHandler(transferHandler);
		Dimension d = new Dimension(50000, 50000);
		customDiagram.setPreferredSize(d);
		
		scrollPane = new JScrollPane(
				customDiagram
		);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		
		pnlControl = new JPanel();
		pnlControl.setLayout(new MigLayout("insets 5 0 5 0, gap 0", "[][]", "[]"));
		pnlControl.setBackground(new Color(101, 45, 146));
		pnlControl.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		add(pnlControl, "cell 0 0 2 1,growx,aligny bottom");
		
		Color cMain=new Color(255,255,255);
		Color cRollover=new Color(230,230,230);
		Color cDisabled=new Color(150,150,150);
		Color cClicked=new Color(200,200,200);
		
		ImageHandler.getScaledImageIcon("res/icons/start.png", 20, 20, Image.SCALE_FAST, (icStart)->{
			if(icStart==null) {
				btnStart = new JButton("Start");
			}else {
				btnStart=new PictureButton(icStart, cMain,cRollover,cClicked,cDisabled);
			}
			btnStart.setFocusable(false);
			btnStart.addActionListener(new BtnStartActionListener());
			
			EventQueue.invokeLater(()->{
				pnlControl.add(btnStart,"cell 0 0");
				revalidate();
				repaint();
			});
			
			return null;
		});
		
		ImageHandler.getScaledImageIcon("res/icons/stop.png", 20, 20, Image.SCALE_FAST, (icStop)->{
			if(icStop==null) {
				btnStop = new JButton("Stop");
			}else {
				btnStop=new PictureButton(icStop, cMain,cRollover,cClicked,cDisabled);
			}
			btnStop.setFocusable(false);
			btnStop.addActionListener(new BtnStopActionListener());
			
			EventQueue.invokeLater(()->{
				pnlControl.add(btnStop,"cell 1 0");
				revalidate();
				repaint();
			});
			
			return null;
		});
		
		this.add(scrollPane, "cell 0 1,grow");
		

		customDiagram.setSize(10000, 100000);

		customDiagram.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (CustomTransferHandler.isBoxCreated()) {
					createNAddDiagramItem(new Point(e.getX(), e.getY()));
				}
			}
		});

		ImageHandler.getImage("res/EVLogo_transparency.jpg",(img)->{
			customDiagram.setBackgroundImage(img);
			
			EventQueue.invokeLater(()->{
				customDiagram.revalidate();
				customDiagram.repaint();
			});

			return null;
		});	
		customDiagram.setOpaque(true);
		customDiagram.setBackground(new Color(231, 231, 234));
		
		pnlLists = new JPanel();
		pnlLists.setOpaque(false);
		this.add(pnlLists, "cell 1 1,growx,aligny top");
		pnlLists.setLayout(new BoxLayout(pnlLists,BoxLayout.Y_AXIS));

		initLists();
		
		customDiagram.addDiagramListener(new DiagramListener() {
			
			@Override
			public void onDeleteItem(DiagramItem diagramItem) {
				ParameterizedObject pObject=getFunctionFromDiagramItem(diagramItem, ParameterizedObject.class);
				if(pObject!=null)
					pObject.stop();
//				Component component = diagramItem.getComponent();
//				MatSender sender = null;
//				ParameterReceiver rec = null;
//
//				if (component instanceof MatReceiverNSenderPanel) {
//					sender=getFunctionFromDiagramItem(diagramItem, MatSender.class);
//
//					if(sender!=null) {
//						sender.stop();
//						sender.clearMatReceiverFunctions();
//					}
//				} else if (component instanceof ParameterReceiverPanel) {
//					rec = (ParameterReceiver) ((ParameterReceiverPanel) component).getFunction();
//				}
//
//				for (DiagramInput input : diagramItem.getInputs()) {
//					for (DiagramItemConnection con : input.getConnections()) {
//						DiagramItem fromItem = con.getFrom().getDiagramItem();
//						Component fromComp = fromItem.getComponent();
//
//						if (fromComp instanceof MatReceiverNSenderPanel) {
//							MatSender fromSender = ((MatReceiverNSenderPanel) fromComp).getFunction();
//							if (sender != null && sender instanceof MatReceiver) {
//								fromSender.removeMatReceiver((MatReceiver) sender);
//							}
//							if (sender != null && sender instanceof MatMapReceiver) {
//								fromSender.removeMatMapReceiver((MatMapReceiver) sender);
//							}
//							if (rec != null && rec instanceof ParameterReceiver) {
//								fromSender.removeParamterReceiver(rec);
//							}
//						}
//					}
//				}
			}

			@Override
			public void onDeleteConnection(DiagramItemConnection connection) {
				DiagramItem to_item = connection.getTo().getDiagramItem();
				DiagramOutput from = connection.getFrom();
				DiagramInput to = connection.getTo();
				DiagramItem from_item = connection.getFrom().getDiagramItem();
				
				if(to_item.getComponent() instanceof MultiMatEditFunctionDiagramPanel) {
					MultiMatEditFunction function=getFunctionFromDiagramItem(to_item, MultiMatEditFunction.class);
					MatSender sender=getFunctionFromDiagramItem(from_item, MatSender.class);
					function.removeMatSender(sender);
				}

				if (!loading) {
					if (from_item.getComponent() instanceof FunctionPanel<?>
							&& to_item.getComponent() instanceof FunctionPanel<?>) {

						FunctionPanel<?> pnl_from = (FunctionPanel<?>) from_item.getComponent();
						FunctionPanel<?> pnl_to = (FunctionPanel<?>) to_item.getComponent();

						if (pnl_from.getFunction() instanceof MatSender) {
							if (pnl_to.getFunction() instanceof MatReceiver) {
								if (to.getName().contains("in")) {
									((MatSender) pnl_from.getFunction())
											.removeMatReceiver((MatReceiver) pnl_to.getFunction());
								}
							}
						}

						if (pnl_from.getFunction() instanceof ParameterizedObject) {
							if (pnl_to.getFunction() instanceof ParameterReceiver) {
								if (to.getName().contains("in")) {
									((ParameterizedObject) pnl_from.getFunction())
											.removeParamterReceiver((ParameterReceiver) pnl_to.getFunction());
								}
							}
						}
					}
				}
			}

			@Override
			public void onCreateConnection(DiagramItemConnection connection) {
				DiagramItem to_item = connection.getTo().getDiagramItem();
				DiagramOutput from = connection.getFrom();
				DiagramInput to = connection.getTo();
				DiagramItem from_item = connection.getFrom().getDiagramItem();
				
				ParameterReceiver rec=getFunctionFromDiagramItem(to_item, ParameterReceiver.class);

				if (rec!=null) {
					if(!from.getName().equals("parameter_out")) {
						connection.setFrom((DiagramOutput) from.getDiagramItem().getConnector("parameter_out"));
					}

					EventQueue.invokeLater(() -> {
						connection.setForeground(Color.BLUE);
						connection.revalidate();
						connection.repaint();
					});
				}
				
				MultiMatEditFunction mmefct = getFunctionFromDiagramItem(to_item, MultiMatEditFunction.class);
				if(mmefct != null) {
					MatSender sender=getFunctionFromDiagramItem(from_item, MatSender.class);
					int index=0;
					if(to.getName().contains("_in_")) {
						index=Integer.parseInt(to.getName().split("_")[2]);
					}
					mmefct.addMatSender(sender, index);
					
					if(to.getName().startsWith("function")) {
						EventQueue.invokeLater(() -> {
							connection.setForeground(Color.RED);
							connection.revalidate();
							connection.repaint();
						});
					}
				}

				if (!loading) {
					if (from_item.getComponent() instanceof FunctionPanel<?>
							&& to_item.getComponent() instanceof FunctionPanel<?>) {

						FunctionPanel<?> pnl_from = (FunctionPanel<?>) from_item.getComponent();
						FunctionPanel<?> pnl_to = (FunctionPanel<?>) to_item.getComponent();

						if (pnl_from.getFunction() instanceof MatSender) {
							if (pnl_to.getFunction() instanceof MatReceiver) {
								if (to.getName().startsWith("input") 
										|| to.getName().contains("mat_in")) {
									((MatSender) pnl_from.getFunction())
											.addMatReceiver((MatReceiver) pnl_to.getFunction());
								}
							}
						}

						if (pnl_from.getFunction() instanceof ParameterizedObject) {
							if (pnl_to.getFunction() instanceof ParameterReceiver) {
								if ("input".equals(to.getName())) {
									((ParameterizedObject) pnl_from.getFunction())
											.addParameterReceiver((ParameterReceiver) pnl_to.getFunction());
								}
							}
						}
					}
				}
			}

			@Override
			public boolean onConnectionAvailable(DiagramItem from, DiagramItem to) {
				
				boolean ret=false;
				
				MultiMatEditFunction multiFunctionTo=getFunctionFromDiagramItem(to, MultiMatEditFunction.class);
				if(multiFunctionTo!=null) {
					
					MatSender sender=getFunctionFromDiagramItem(from, MatSender.class);
					
					if(multiFunctionTo.getSenderIndex().containsKey(sender)) {
						return false;
					}
				}
				
				List<DiagramConnector> cons=new ArrayList<>(to.getConnectors().values());
				for(int i=cons.size()-1;i>=0;i--) {
					DiagramConnector con=cons.get(i);
					if(con instanceof DiagramInput) {
						DiagramInput input=(DiagramInput) con;
						if(input.isConnectionAllowed()) {
							to.setSelectedInput(input.getName());
							ret = true;
						}
					}
				}
				
				if(ret) {
					MatReceiver matRec=getFunctionFromDiagramItem(to, MatReceiver.class);
					Color c=Color.GREEN;
					
					if(matRec!=null) {
						if(!from.getSelectedOutput().getName().equals("output")) {
							from.setSelectedOutput("output");
						}
						
						if(to.getSelectedInput().getName().startsWith("function")) {
							c=Color.RED;
						}
						
					}else {
						ParameterReceiver rec=getFunctionFromDiagramItem(to, ParameterReceiver.class);
						if(rec!=null) {
							if(!from.getSelectedOutput().getName().equals("parameter_out")) {
								from.setSelectedOutput("parameter_out");
							}
							c=Color.BLUE;
						}
					}
					
					final Color c2=c;
					EventQueue.invokeLater(()->{
						from.setBorder(c2, 5);
						from.revalidate();
						from.repaint();
						
						to.setBorder(c2, 5);
						to.revalidate();
						to.repaint();								
					});
				}
				
				return ret;
			}

			@Override
			public void onCreateItem(DiagramItem diagramItem) {
			}

			@Override
			public void onCopied(DiagramItem diagramItem) {
				JComponent comp = diagramItem.getComponent();
				if (comp instanceof FunctionPanel<?>) {
					ParameterizedObject po = (ParameterizedObject) ((FunctionPanel) comp).getFunction();
					String s = Serializing.serialize(po);

					StringSelection selection = new StringSelection(s);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
				}
			}

			@Override
			public void onPasted(Transferable t, Point mouseLocation) {
				try {
					String s = (String) t.getTransferData(DataFlavor.stringFlavor);
					Object o = Serializing.deSerialize(s);
					if (o instanceof MatSender) {
						((MatSender) o).clearMatReceivers();
						((MatSender) o).clearParameterReceivers();
						addMatSenderDiagramItem(mouseLocation, (MatSender) o);
						((MatSender) o).recalculateId();
						if(o instanceof MultiMatEditFunction) {
							((MultiMatEditFunction) o).clearMatSenderIndex();
						}
					} else if (o instanceof ParameterReceiver) {
						addParamReceiverDiagramItem(mouseLocation, (ParameterReceiver) o);
					}
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onItemSelectionChanged(DiagramItem diagramItem, boolean selected) {
				if(selected) {
					selectedStartable=getFunctionFromDiagramItem(diagramItem, Startable.class);
				}else {
					selectedStartable=null;
				}
				
				if(selectedStartable!=null) {
					boolean started=selectedStartable.isStarted();
					
					btnStart.setEnabled(!started);
					btnStop.setEnabled(started);
				}else {
					btnStart.setEnabled(true);
					btnStop.setEnabled(true);
				}
			}

		});
	}

	private void addList(String name, Collection<?> items, ListCellRenderer<Object> cellRenderer) {
		JPanel pnl = new JPanel();
		pnl.setOpaque(false);
		pnl.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnl.setLayout(new MigLayout("gap 0", "[grow]", "[][]"));

		JListHeader header = new JListHeader(name);
		header.setBackground(new Color(247, 148, 29));
		pnl.add(header, "cell 0 0,grow");

		DefaultListModel<Object> listModel = new DefaultListModel<>();
		for (Object item : items) {
			listModel.addElement(item);
		}

		JList<Object> controlList = new JList<>();
		controlList.setOpaque(false);
		controlList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		controlList.setModel(listModel);

		JScrollPane scrollPane = new JScrollPane(controlList) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				setBorder(null);
				if(getHeight()>getViewport().getHeight()) {
					setMaximumSize(new Dimension(100000, controlList.getHeight()+3));
				}
			}
			
			@Override
			public void setVisible(boolean visible) {
				if(!visible)
					setMaximumSize(new Dimension(0,0));
				else
					setMaximumSize(new Dimension(100000, controlList.getHeight()+3));
				super.setVisible(visible);
			}
		};
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		header.setOnInflate((inflated)->{
			scrollPane.setVisible(inflated);
			return null;
		});

		pnl.add(scrollPane, "cell 0 1,grow");
//
		if (cellRenderer != null)
			controlList.setCellRenderer(cellRenderer);
		controlList.setDragEnabled(true);
		controlList.setTransferHandler(transferHandler);
		pnlLists.add(pnl);

		lists.add(controlList);
	}

	private void initLists() {
		ClassListCellRenderer cellRenderer = new ClassListCellRenderer();
		addList("Streamers", MainFrame.getMatStreamerClasses(), cellRenderer);
		addList("Cv-Functions", MainFrame.getMatEditFunctionClasses(), cellRenderer);
		addList("Multi-Cv-Functions", MainFrame.getMultiMatEditFunctionClasses(), cellRenderer);
		addList("Param-Functions", MainFrame.getParameterReceiverClasses(), cellRenderer);
	}

	private <T> T initFunction(Class<T> functionClass) {
		T function = null;

		try {
			function = functionClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return function;
	}

	private void createNAddDiagramItem(Point pos) {
		MatSender sender = null;
		ParameterReceiver paramRec = null;

		for (JList<?> list : lists) {
			if (list.getSelectedValue() != null) {
				Object o = list.getSelectedValue();

				if (o instanceof Class<?>) {
					for (Class<? extends MatEditFunction> fctClass : MainFrame.getMatEditFunctionClasses()) {
						if (fctClass.equals(o)) {
							sender = initFunction(fctClass);
							break;
						}
					}

					if (sender == null) {
						for (Class<? extends ParameterReceiver> fctClass : MainFrame.getParameterReceiverClasses()) {
							if (fctClass.equals(o)) {
								paramRec = initFunction(fctClass);
								break;
							}
						}
					}

					if (sender == null) {
						for (Class<? extends MatStreamer> fctClass : MainFrame.getMatStreamerClasses()) {
							if (o.equals(fctClass)) {
								sender = initFunction(fctClass);
								break;
							}
						}
					}
					
					if (sender == null) {
						for (Class<? extends MultiMatEditFunction> fctClass : MainFrame.getMultiMatEditFunctionClasses()) {
							if (o.equals(fctClass)) {
								sender = initFunction(fctClass);
								break;
							}
						}
					}
				}

				list.clearSelection();
			}
		}

		if (sender != null) {
			addMatSenderDiagramItem(pos, sender);
		} else if (paramRec != null) {
			addParamReceiverDiagramItem(pos, paramRec);
		}
	}

	private DiagramItem addMatSenderDiagramItem(Point pos, MatSender sender) {
		DiagramItem item;
		MatReceiverNSenderPanel panel;
		
		if (sender instanceof MatStreamer) {
			panel = new StreamerPanel((MatStreamer) sender);
		} else if (sender instanceof MatEditFunction) {
			panel = new MatEditFunctionDiagramPanel((MatEditFunction) sender);
		} else if (sender instanceof MultiMatEditFunction) {
			panel = new MultiMatEditFunctionDiagramPanel((MultiMatEditFunction) sender);
		} else {
			panel = new MatReceiverNSenderPanel(sender, "Name") {
			};
		}

		item = new DiagramItem(panel, customDiagram);

		if (panel instanceof StreamerPanel) {
			item.getSelectedInput().setMaxConnectionNumber(0);
		} else if(panel instanceof MultiMatEditFunctionDiagramPanel){
			item.getSelectedInput().setMaxConnectionNumber(1);
			//TODO: Add different inputs
			int nrMatInputs=((MultiMatEditFunction)sender).getNrMatInputs();
			for(int i=1;i<nrMatInputs;i++) {
				final int i1=i;
				DiagramInput input = new DiagramInput(item, (nix) -> {
					int x = item.getX();
					int y = item.getY() + item.getHeight() / 2 + i1*10;
					return new Point(x, y);
				},"mat_in_"+i1);
				
				input.setMaxConnectionNumber(1);
				item.addDiagramConnector(input);
			}
			
			int nrFunctionInputs=((MultiMatEditFunction)sender).getNrFunctionInputs();
			for(int i=0;i<nrFunctionInputs;i++) {
				final int i1=i+nrMatInputs;
				DiagramInput input = new DiagramInput(item, (nix) -> {
					int x = item.getX();
					int y = item.getY() + item.getHeight() / 2 + i1*10;
					return new Point(x, y);
				},"function_in_"+i1);
				
				input.setMaxConnectionNumber(1);
				item.addDiagramConnector(input);
			}
			
			
		} else {
			item.getSelectedInput().setMaxConnectionNumber(1);
			if (panel instanceof MatEditFunctionDiagramPanel) {
				MatEditFunction function = (MatEditFunction) ((MatEditFunctionDiagramPanel) panel).getFunction();
				if (!function.isSend()) {
					item.getSelectedOutput().setMaxConnectionNumber(0);
				}
			}
		}

		EventQueue.invokeLater(() -> {
			customDiagram.addDiagramItem(item, pos);
			item.setSize(itemSize);
		});
		
		item.addDiagramConnector(new DiagramOutput(item, (nix) -> {
			int x = item.getX() + item.getWidth();
			int y = item.getY() + item.getHeight() / 2 + 10;
			return new Point(x, y);
		}, "matmap_out"));

		item.addDiagramConnector(new DiagramOutput(item, (nix) -> {
			int x = item.getX() + item.getWidth();
			int y = item.getY() + item.getHeight() / 2 + 20;
			return new Point(x, y);
		}, "parameter_out"));

		if (sender instanceof MatMapReceiver) {
			item.addDiagramConnector(new DiagramInput(item, (nix) -> {
				int x = item.getX();
				int y = item.getY() + item.getHeight() / 2 + 10;
				return new Point(x, y);
			}, "matmap_in"));
		}
		if (sender instanceof ParameterReceiver) {
			item.addDiagramConnector(new DiagramInput(item, (nix) -> {
				int x = item.getX();
				int y = item.getY() + item.getHeight() / 2 + 20;
				return new Point(x, y);
			}, "parameter_in"));
		}

		CustomTransferHandler.cleanText();
		CustomTransferHandler.setBoxCreate(false);

		EventQueue.invokeLater(() -> {
			customDiagram.revalidate();
			customDiagram.repaint();
		});

		return item;
	}

	private DiagramItem addParamReceiverDiagramItem(Point pos, ParameterReceiver receiver) {
		DiagramItem item;
		JComponent panel;

		panel = new ParameterReceiverPanel(receiver, receiver.getClass().getSimpleName());// new
																							// MatReceiverNSenderPanel(receiver,"Name")
																							// {};

		item = new DiagramItem(panel, customDiagram);

		item.getSelectedInput().setMaxConnectionNumber(20);
		item.getSelectedOutput().setMaxConnectionNumber(0);

		customDiagram.addDiagramItem(item, pos);
		item.setSize(itemSize);

		CustomTransferHandler.cleanText();
		CustomTransferHandler.setBoxCreate(false);

		EventQueue.invokeLater(() -> {
			customDiagram.revalidate();
			customDiagram.repaint();
		});

		return item;
	}

	private ArrayList<MatSender> createFunctions() {
		ArrayList<MatSender> ret = new ArrayList<>();

		List<DiagramItem> diagramItems = customDiagram.getDiagramItems();

		for (DiagramItem item : diagramItems) {
			MatSender matSender = null;

			Component component = item.getComponent();

			if (component instanceof StreamerPanel) {
				StreamerPanel streamerPanel = (StreamerPanel) component;
				matSender = streamerPanel.getFunction();
			} else if (component instanceof MatEditFunctionDiagramPanel) {
				MatEditFunctionDiagramPanel panel = (MatEditFunctionDiagramPanel) component;
				matSender = panel.getFunction();
			} else {
				continue;
			}

			if (matSender != null) {
				matSender.clearMatReceiverFunctions();

				List<DiagramItemConnection> links = item.getOutgoingConnections();
				for (DiagramItemConnection link : links) {
					DiagramItem itemTo = link.getTo().getDiagramItem();

					Component destComponent = itemTo.getComponent();
					if (destComponent instanceof MatEditFunctionDiagramPanel) {
						MatReceiver matReceiver = null;

						MatEditFunctionDiagramPanel destPanel = (MatEditFunctionDiagramPanel) destComponent;
						MatEditFunction destFunction = (MatEditFunction) destPanel.getFunction();

						matReceiver = destFunction;

						matSender.addMatReceiver(matReceiver);

						// TODO: for other MatReceivers
					} else if (destComponent instanceof ParameterReceiverPanel) {
						ParameterReceiver paramReceiver = null;

						ParameterReceiverPanel destPanel = (ParameterReceiverPanel) destComponent;
						paramReceiver = (ParameterReceiver) destPanel.getFunction();

						matSender.addParameterReceiver(paramReceiver);
					}

				}
				ret.add(matSender);
			}

		}
		return ret;
	}
	
	private <T> T getFunctionFromComponent(Component component, Class<T> c) {
		if (component instanceof FunctionPanel<?>) {
			FunctionPanel<?> fctPanel = (FunctionPanel<?>) component;
			Object o = fctPanel.getFunction();
			if(c.isAssignableFrom(o.getClass())) {
				return (T)o;
			}
		}
		
		return null;
	}
	
	private <T> T getFunctionFromDiagramItem(DiagramItem item, Class<T> c) {
		return getFunctionFromComponent(item.getComponent(), c);
	}
	
	private ArrayList<Startable> getStartables(){
		ArrayList<Startable> ret = new ArrayList<>();
		
		List<DiagramItem> diagramItems = customDiagram.getDiagramItems();

		for (DiagramItem item : diagramItems) {
			Startable startable=getFunctionFromDiagramItem(item, Startable.class);
			if(startable!=null) {
				ret.add(startable);
			}
		}
		
		return ret;
	}
	
	private ArrayList<MatStreamer> getMatStreamers(){
		ArrayList<MatStreamer> ret = new ArrayList<>();
		
		List<DiagramItem> diagramItems = customDiagram.getDiagramItems();

		for (DiagramItem item : diagramItems) {
			MatStreamer streamer=getFunctionFromDiagramItem(item, MatStreamer.class);
			if(streamer!=null) {
				ret.add(streamer);
			}
		}
		
		return ret;
	}

	private class BtnStartActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(selectedStartable!=null) {
				selectedStartable.start();
				
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
			}else {
				getStartables().forEach((startable)->{
					startable.start();
				});
			}
		}
	}
	
	private class BtnStopActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			
			if(selectedStartable!=null) {
				selectedStartable.stop();
				
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}else {
				getStartables().forEach((startable)->{
					startable.stop();
				});
			}
		}
	}

	public List<MatSender> getMatSenders() {
		List<MatSender> ret = new ArrayList<>();
		List<DiagramItem> diagramItems = customDiagram.getDiagramItems();

		for (DiagramItem item : diagramItems) {
			MatSender matSender = null;
			Component component = item.getComponent();

			if (component instanceof MatReceiverNSenderPanel) {
				MatReceiverNSenderPanel panel = (MatReceiverNSenderPanel) component;
				matSender = panel.getFunction();
				ret.add(matSender);
			}
		}

		return ret;
	}
	
	public void save(File file) {
		new Thread(()->{
			saveSynch(file);
		}).start();
	}

	
	private synchronized void saveSynch(File file) {
		if (file != null) {
			createFunctions();
			getMatStreamers().forEach((streamer)->{
				streamer.stop();
			});

			ArrayList<Serializable> functions = new ArrayList<>();
			List<DiagramItem> diagramItems = customDiagram.getDiagramItems();

			for (DiagramItem item : diagramItems) {
				Serializable function;
				Component component = item.getComponent();

				if (item.getSelectedInput().getConnections().size() < 1) {
					if (component instanceof FunctionPanel<?>) {
						FunctionPanel<?> panel = (FunctionPanel<?>) component;
						if(panel.getFunction() instanceof Serializable) {
							function = (Serializable) panel.getFunction();
							functions.add(function);
						}
					}
				}
			}

			Serializing.serialize(functions, file);
		}
	}
	
	
	public void load(File file) {
		new Thread(()->{
			loadSynch(file);
		}).start();
	}
	

	@SuppressWarnings("unchecked")
	private synchronized void loadSynch(File file) {
		loading = true;
		if (file != null) {
			this.clear();
			ArrayList<Serializable> senders;
			try {
				senders = (ArrayList<Serializable>) Serializing.deSerialize(file);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (senders != null) {
				createDiagram(senders);
			}
		}
		loading = false;
	}
	
	public void clear() {
		customDiagram.clear();
	}

	private void createDiagram(ArrayList<Serializable> senders) {
		int posX = 5; // Startposition x
		int posY = 5; // Startposition y
		Map<Object, DiagramItem> functions=new HashMap<>(); // List for functions, to check, if function has been added before

		for (Serializable sender : senders) { // iterating over all serializables
			posY = handleDiagramCreationTree(posX, posY, sender, posY, null, functions) + 30; 
		}
		
		
		//FUNCTIONS WITH MORE INPUTS
		for(Object o:functions.keySet()) {
			
			if(o instanceof Switch) {
				Switch.setInstance((Switch) o);
			}
			//MultiMatSender sender creation
			if(o instanceof MultiMatEditFunction) {
				DiagramItem itemMmefct=functions.get(o);
				MultiMatEditFunction mmefct=(MultiMatEditFunction)o;
				for(MatSender sender:mmefct.getSenderIndex().keySet()) {
					if(functions.containsKey(sender)) {
						int index=mmefct.getIndexOfMatsender(sender);
						DiagramItem itemSender=functions.get(sender);
						itemMmefct.setSelectedInput(index);
						itemSender.connectTo(itemMmefct);
					}
				}
			}
			
			else if(o instanceof ParameterReceiver) {
				ParameterizedObject po=(ParameterizedObject)o;
				DiagramItem di1=functions.get(po);
				
				
				for(Object x:functions.keySet()) {
					if(x instanceof ParameterizedObject) {
						if(((ParameterizedObject) x).getParamReceivers().contains(po)) {
							DiagramItem di2=functions.get(x);
							di2.connectTo(di1);
						}
					}
				}
			}
		}
	}

	private int handleDiagramCreationTree(int posX, int posY, Object diagramObject, int highestY,
			DiagramItem ancestorItem, Map<Object, DiagramItem> functions) {
		
		MatSender sender = null;
		ParameterReceiver paramReceiver = null;
		if (diagramObject instanceof MatSender) {
			sender = (MatSender) diagramObject;
		}
		if (diagramObject instanceof ParameterReceiver) {
			paramReceiver = (ParameterReceiver) diagramObject;
		}
		

		Point p = new Point(posX, posY);
		DiagramItem item = null;

		if (sender != null) {
			if(sender instanceof UniqueFunction) {
				if(!functions.keySet().contains(sender)) {
					item = addMatSenderDiagramItem(p, sender);
					functions.put(sender, item);
				}
			}else {
				item = addMatSenderDiagramItem(p, sender);
			}
		} else if (paramReceiver != null) {
			if(paramReceiver instanceof UniqueFunction) {
				if(!functions.keySet().contains(paramReceiver)) {
					item = addParamReceiverDiagramItem(p, paramReceiver);
					functions.put(sender, item);
				}
			}else {
				item = addParamReceiverDiagramItem(p, paramReceiver);
			}
		}
		
		if(item==null)
			return highestY;
		else {
			functions.put(diagramObject, item);
		}
		
		int ownY = posY + item.getHeight();
		highestY = ownY > highestY ? ownY : highestY;

		if (ancestorItem != null) {
			MultiMatEditFunction mmefct=getFunctionFromDiagramItem(item, MultiMatEditFunction.class);
			if(mmefct==null && paramReceiver==null) {
				ancestorItem.connectTo(item);
			}
		}

		int i = 0;

		if (sender != null) {
			List<MatReceiver> receiversToRemove = new ArrayList<>();
			for (MatReceiver receiver : sender.getReceivers()) {
				if (receiver instanceof MatSender) {
					int y = handleDiagramCreationTree(posX + (int)itemSize.getWidth() + 30, posY + (i * (30 + (int)itemSize.getHeight())),
					(MatSender) receiver, highestY, item, functions);
					highestY = y > highestY ? y : highestY;
					i++;
				} else {
					receiversToRemove.add(receiver);
				}
			}
			for (ParameterReceiver receiver : sender.getParamReceivers()) {
				int y = handleDiagramCreationTree(posX + (int)itemSize.getWidth() + 30, posY + (i * (30 + (int)itemSize.getHeight())),
				(ParameterReceiver) receiver, highestY, item, functions);
				highestY = y > highestY ? y : highestY;
				i++;
			}
			sender.getReceivers().removeAll(receiversToRemove);
		}
		return highestY;
	}
}
