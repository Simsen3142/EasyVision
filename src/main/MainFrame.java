package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Modifier;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIDefaults;

import org.opencv.core.Core;
import org.reflections.Reflections;

import database.OftenUsedObjects;
import database.Serializing;
import diagramming.DiagramPanel;
import functions.matedit.MatEditFunction;
import functions.matedit.multi.MultiMatEditFunction;
import functions.streamer.FileVideoStreamer;
import functions.streamer.MatStreamer;
import functions.streamer.VideoStreamer;
import main.menu.MainMenuBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.UIManager;
import javax.swing.JMenuBar;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient JPanel contentPane;
	private JPanel mainPanel;
	private transient MainMenuBar mainMenuBar;
	private transient JMenuBar menuBar;
	private ThisWindowListener windowListener;

	private static MainFrame instance;
	private static Set<Class<? extends MatStreamer>> matStreamerClasses;
	private static Set<Class<? extends MatEditFunction>> matEditFunctionClasses;
	private static Set<Class<? extends MultiMatEditFunction>> multiMatEditFunctionClasses;
	private static Set<Class<? extends ParameterReceiver>> parameterReceiverClasses;
	private static DiagramPanel diagramPanel;
	private File fileCurrentDiagram;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		long start=System.currentTimeMillis();
		long actualStart=start;
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.getDefaults().put("ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {}));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String bit=System.getProperty("sun.arch.data.model");
		String folder=bit.equals("64")?"x64/":"x86/";
		String userdir = System.getProperty("user.dir");
		String opencvpath = userdir + "/opencv/build/java/"+folder;
		
		System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
		
		System.load(System.getProperty("user.dir") + "/opencv/build/bin/opencv_ffmpeg341"+(bit.equals("64")?"_64":"") +".dll");
		
//		System.load(userdir+"\\arduino\\rxtxParallel.dll");
//		System.load(userdir+"\\arduino\\rxtxSerial.dll");
		
		System.loadLibrary("rxtxParallel");
		System.loadLibrary("rxtxSerial");
		
		System.out.println("LOADING LIBRARIES: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		
		initReflectionClasses();
		
		System.out.println("REFLECTIONS: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		
		initPreSetValues();

		System.out.println("INIT MAINFRAME: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		
//		for (int i = 0; true; i++) {
//			VideoStreamer videoStreamer = new VideoStreamer(i);
//			if (videoStreamer.getCamera()!=null && videoStreamer.getCamera().isOpened()) {
//				streamers.add(videoStreamer);
//				videoStreamer.start();
//			}else {
//				break;
//			}
//
//		}
		
		
//        VideoStreamer videoStreamer=new VideoStreamer(0);
//        streamers.add(videoStreamer);
//		videoStreamer.addMatReceiver(functions.get(0));
		
//        FileVideoStreamer videoStreamer_video=new FileVideoStreamer(new File("test/SampleVideo_1280x720_1mb.mp4"));
//        streamers.add(videoStreamer_video);

		
		diagramPanel = new DiagramPanel();
		
		System.out.println("INIT DIAGRAM: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		
		instance.setContentPanel(diagramPanel);
		
		System.out.println("SET CONTENTPANE: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		
		loadValues();
		
		System.out.println("LOAD SESSION: "+(System.currentTimeMillis()-start));
		System.out.println("===============");
		System.out.println("STARTUP: "+(System.currentTimeMillis()-actualStart));
	}
	
	/**
	 * @return the mainMenuBar
	 */
	public MainMenuBar getMainMenuBar() {
		return mainMenuBar;
	}

	/**
	 * @return the matEditFunctionClasses
	 */
	public static Set<Class<? extends MatEditFunction>> getMatEditFunctionClasses() {
		if (matEditFunctionClasses == null)
			initReflectionClasses();
		return matEditFunctionClasses;
	}
	
	/**
	 * @return the multiMatEditFunctionClasses
	 */
	public static Set<Class<? extends MultiMatEditFunction>> getMultiMatEditFunctionClasses() {
		if (multiMatEditFunctionClasses == null)
			initReflectionClasses();
		return multiMatEditFunctionClasses;
	}
	
	/**
	 * @return the matEditFunctionClasses
	 */
	public static Set<Class<? extends ParameterReceiver>> getParameterReceiverClasses() {
		if (parameterReceiverClasses == null)
			initReflectionClasses();
		return parameterReceiverClasses;
	}

	/**
	 * @return the streamers
	 */
	public static Set<Class<? extends MatStreamer>> getMatStreamerClasses() {
		if (matStreamerClasses == null)
			initReflectionClasses();
		return matStreamerClasses;
	}
	
	private static void initReflectionClasses() {
		matEditFunctionClasses=new HashSet<>();
		multiMatEditFunctionClasses=new HashSet<>();
		parameterReceiverClasses=new HashSet<>();
		matStreamerClasses=new HashSet<>();
		
		Reflections reflections = new Reflections("functions.matedit");
		reflections.getSubTypesOf(MatEditFunction.class).forEach((clss)->{
			if(!Modifier.isAbstract(clss.getModifiers())) {
				matEditFunctionClasses.add(clss);
			}
		});
		
		reflections = new Reflections("functions.matedit.multi");
		reflections.getSubTypesOf(MultiMatEditFunction.class).forEach((clss)->{
			if(!Modifier.isAbstract(clss.getModifiers())) {
				multiMatEditFunctionClasses.add(clss);
			}
		});
		
		reflections = new Reflections("functions.parameterreceiver");
		reflections.getSubTypesOf(ParameterReceiver.class).forEach((clss)->{
			if(!Modifier.isAbstract(clss.getModifiers())) {
				parameterReceiverClasses.add(clss);
			}
		});
		
		reflections = new Reflections("functions.streamer");
		reflections.getSubTypesOf(MatStreamer.class).forEach((clss)->{
			if(!Modifier.isAbstract(clss.getModifiers())) {
				matStreamerClasses.add(clss);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		boolean first = instance == null;
		if (first) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setTitle("Main Frame");
			addWindowListener(windowListener = new ThisWindowListener());
		} else {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		setBounds(600, 300, 450, 300);

		mainMenuBar=new MainMenuBar();
		menuBar = mainMenuBar.getJMenuBar();
		setJMenuBar(menuBar);
		initDiagramMenuFunctions();

		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
		setContentPane(contentPane);

		contentPane.setLayout(new BorderLayout(0, 0));

		mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);

		setVisible(true);
	}
	
	private void initDiagramMenuFunctions() {
		mainMenuBar.addNewActionListener((e)->{
			diagramPanel.clear();
			fileCurrentDiagram=null;
		});
		
		mainMenuBar.addOpenActionListener((e)->{
			File f=Serializing.showOpenDialog();
			fileCurrentDiagram=f;
			diagramPanel.load(f);
		});
		
		mainMenuBar.addOpenLastActionListener((e)->{
			fileCurrentDiagram=null;
			try {
				diagramPanel.load(OftenUsedObjects.SESSION.getFile());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		mainMenuBar.addSaveAsActionListener((e)->{
			File f=Serializing.showSaveDialog();
			fileCurrentDiagram=f;
			diagramPanel.save(f);
		});
		
		mainMenuBar.addSaveActionListener((e)->{
			File f;
			f=(fileCurrentDiagram==null)?Serializing.showSaveDialog():fileCurrentDiagram;
			diagramPanel.save(f);
		});
	}
	
	

	public void setContentPanel(JPanel content) {
		contentPane.remove(mainPanel);
		mainPanel = content;
		contentPane.add(mainPanel, BorderLayout.CENTER);

		EventQueue.invokeLater(()->{
			contentPane.revalidate();
			contentPane.repaint();
		});
	}

	public static void initPreSetValues() {
		instance = new MainFrame();
	}

	@SuppressWarnings("unchecked")
	public static void loadValues() {
		try {
			diagramPanel.load(OftenUsedObjects.SESSION.getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveValues() {
		diagramPanel.save(OftenUsedObjects.SESSION.getFile());
	}

	private static class ThisWindowListener extends WindowAdapter implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("SAVING...");
			saveValues();
			diagramPanel.getMatSenders().forEach((matSender)->matSender.stop());
			System.exit(0);
		}
	}
}
