package diagramming.old;

import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Set;

import javax.swing.*;
import javax.swing.SpringLayout;
import javax.swing.event.*;

import com.mindfusion.drawing.*;

import components.MatEditFunctionClassListCellRenderer;
import cvfunctions.MatEditFunction;
import diagramming.CustomTransferHandler;
import diagramming.components.CVFunctionPanel;
import diagramming.components.StreamerPanel;
import main.MainFrame;
import main.MatSender;
import main.VideoStreamer;

import com.mindfusion.diagramming.*;
import net.miginfocom.swing.MigLayout;
import view.MatReceiverPanel;
import view.PanelFrame;

public class FormEditorFrame extends JFrame {
	private JList<Class<? extends MatEditFunction>> fct_controlList;
	private JList<VideoStreamer> strmer_controlList;
	private Diagram diagram;
	private DiagramView diagramView;
	private JFileChooser fileDialog;
	private CustomTransferHandler transferHandler;
	private JScrollPane scrollPane;
//	private PropertyGrid propertyGrid;

	private static final long serialVersionUID = 1L;

	public FormEditorFrame() {
		super("MindFusion.Diagramming Sample: Form Editor");
		initDiagram();
		initFrame();
	}

	private void initDiagram() {
		transferHandler = new CustomTransferHandler();

		// diagram
		diagram = new Diagram();
		diagram.setShowGrid(true);
		diagram.setBackBrush(new SolidBrush(new Color(220, 220, 220)));

		diagram.addDiagramListener(new DiagramAdapter() {
			public void nodeSelected(NodeEvent e) {
			}

			@Override
			public void linkCreated(LinkEvent arg0) {
				checkLinks();
			}

			@Override
			public void linkDeleted(LinkEvent arg0) {
				checkLinks();
			}

			@Override
			public void linkModified(LinkEvent arg0) {
				checkLinks();
			}

			@Override
			public void viewportChanged() {
			}
		});

		// diagramView
		diagramView = new DiagramView(diagram);
		diagramView.setBehavior(Behavior.LinkControls);
		diagramView.setTransferHandler(transferHandler);

		diagramView.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (CustomTransferHandler.isBoxCreated()) {
					nodeCreating(e);
				}
			}
		});
	}

	private void checkLinks() {
		DiagramItemList items = diagram.getItems();
		for (DiagramItem item : items) {
			if (item instanceof ControlNode) {
				ControlNode node = (ControlNode) item;
				boolean allowIncominLinks = node.getAllIncomingLinks().size() < 1;
				node.setAllowIncomingLinks(allowIncominLinks);

				if (node.getControl() instanceof StreamerPanel) {
					node.setAllowIncomingLinks(false);
				}
			}
		}
	}

	protected void nodeCreating(MouseEvent e) {
		Point boxDevPos = new Point(e.getX(), e.getY());
		Point2D.Float boxDocPos = diagramView.deviceToDoc(boxDevPos);

		JComponent component = new JButton(CustomTransferHandler.getText());
		ControlNode node = new ControlNode(diagramView);

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
				node.setAllowIncomingLinks(false);
				break;
			}
		}

		node.setControl(component);
		node.setBounds(((int) boxDocPos.getX() - 10), ((int) boxDocPos.getY() - 10), 20, 20);
		diagram.add(node);

		CustomTransferHandler.cleanText();
		CustomTransferHandler.setBoxCreate(false);
	}

	protected void loadDiagram() {
		if (fileDialog.showOpenDialog(FormEditorFrame.this) == JFileChooser.APPROVE_OPTION) {
			try {
				diagram.loadFrom(fileDialog.getSelectedFile().getAbsolutePath());
			} catch (Exception exp) {
				exp.getMessage();
			}
		}
	}

	protected void saveDiagram() {
		if (fileDialog.showSaveDialog(FormEditorFrame.this) == JFileChooser.APPROVE_OPTION) {
			try {
				diagram.saveTo(fileDialog.getSelectedFile().getAbsolutePath());
			} catch (FileNotFoundException exp) {
				exp.getMessage();
			} catch (IOException exp) {
				exp.getMessage();
			}
		}
	}

	private void initFrame() {
		fileDialog = new JFileChooser();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// scrollPane
		scrollPane = new JScrollPane(diagramView);

		// right panel
		JPanel rightPanel = new JPanel();
		JPanel rightPanelTop = new JPanel();
		rightPanelTop.setLayout(new MigLayout("", "[130px,grow][130px,grow]", "[25px][10px][][25px,grow]"));

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveDiagram();
			}
		});
		rightPanelTop.add(btnSave, "cell 1 0,grow");

		JPanel panel = new JPanel();
		rightPanelTop.add(panel, "cell 0 0 1 4,grow");
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel lblKameras = new JLabel("Kameras");
		lblKameras.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblKameras);

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
		panel.add(strmer_controlList);

		strmer_controlList.setModel(strmer_model);
		strmer_controlList.setDragEnabled(true);
		strmer_controlList.setTransferHandler(transferHandler);

		JLabel lblFunktionen = new JLabel("Funktionen");
		lblFunktionen.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblFunktionen);

		fct_controlList = new JList<Class<? extends MatEditFunction>>();
		panel.add(fct_controlList);

		fct_controlList.setModel(fct_model);
		fct_controlList.setCellRenderer(new MatEditFunctionClassListCellRenderer());
		fct_controlList.setDragEnabled(true);
		fct_controlList.setTransferHandler(transferHandler);

		// arrange the children of the right panel
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.SOUTH, rightPanelTop, 511, SpringLayout.NORTH, rightPanel);
		layout.putConstraint(SpringLayout.EAST, rightPanelTop, 0, SpringLayout.EAST, rightPanel);
		layout.putConstraint(SpringLayout.NORTH, rightPanelTop, 0, SpringLayout.NORTH, rightPanel);
		layout.putConstraint(SpringLayout.WEST, rightPanelTop, 0, SpringLayout.WEST, rightPanel);

		rightPanel.add(rightPanelTop);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadDiagram();
			}
		});
		rightPanelTop.add(btnLoad, "cell 1 2,grow");
		
		JButton btnActivate = new JButton("Activate");
		btnActivate.addActionListener(new BtnActivateActionListener());
		rightPanelTop.add(btnActivate, "cell 1 3,growx");
		rightPanel.setLayout(layout);

		// layout the scrollpane and the right panel
		Container cp = getContentPane();

		layout = new SpringLayout();
		layout.putConstraint(SpringLayout.EAST, scrollPane, -300, SpringLayout.EAST, cp);
		layout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, cp);
		layout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, cp);
		layout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, cp);

		layout.putConstraint(SpringLayout.EAST, rightPanel, 0, SpringLayout.EAST, cp);
		layout.putConstraint(SpringLayout.NORTH, rightPanel, 0, SpringLayout.NORTH, cp);
		layout.putConstraint(SpringLayout.WEST, rightPanel, 0, SpringLayout.EAST, scrollPane);
		layout.putConstraint(SpringLayout.SOUTH, rightPanel, 0, SpringLayout.SOUTH, cp);

		cp.add(scrollPane);
		cp.add(rightPanel);

		cp.setLayout(layout);

		setSize(600, 550);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	private ArrayList<MatSender> createFunctions() {
		ArrayList<MatSender> ret=new ArrayList<>();
		
		DiagramItemList diagramItems=diagram.getItems();
		
		for(DiagramItem item:diagramItems) {
			if(item instanceof ControlNode) {
				MatSender sender=null;

				ControlNode node=(ControlNode)item;
				Component component=node.getControl();
				
				if(component instanceof StreamerPanel) {
					StreamerPanel streamerPanel=(StreamerPanel)component;
					sender=streamerPanel.getMatSender();
				}else if(component instanceof CVFunctionPanel) {
					CVFunctionPanel panel=(CVFunctionPanel)component;
					sender=panel.getMatSender();
				}
				
				ret.add(sender);
				
				DiagramLinkList links=node.getAllOutgoingLinks();
				for (DiagramLink link : links) {
					DiagramNode linkNode=link.getDestination();
					if(linkNode instanceof ControlNode) {
						ControlNode linkCntNd=(ControlNode)linkNode;
						Component destComponent=linkCntNd.getControl();
						if(destComponent instanceof CVFunctionPanel) {
							CVFunctionPanel destPanel=(CVFunctionPanel)destComponent;
							MatEditFunction destFunction=(MatEditFunction)destPanel.getMatSender();
							
							sender.addMatReceiver(destFunction);
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	private class BtnActivateActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			ArrayList<MatSender> senders=createFunctions();
			
			for(MatSender sender:senders) {
				new PanelFrame(new MatReceiverPanel(sender)).setVisible(true);
			}
		}
	}
}