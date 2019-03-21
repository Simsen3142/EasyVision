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
	private static Set<Object> knownCameraResources;
	private transient JMenuBar menuBar;
	private ThisWindowListener windowListener;

	private static MainFrame instance;
	private static Set<Class<? extends MatStreamer>> matStreamerClasses;
	private static Set<Class<? extends MatEditFunction>> matEditFunctionClasses;
	private static Set<Class<? extends ParameterReceiver>> parameterReceiverClasses;
	private static DiagramPanel diagramPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		
		System.out.println(System.getProperty("java.library.path"));

		System.loadLibrary("rxtxParallel");
		System.loadLibrary("rxtxSerial");
		
		System.out.println(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
		System.out.println(userdir+"/arduino/rxtxSerial.dll");

		initReflectionClasses();
		initPreSetValues();

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
		instance.setContentPanel(diagramPanel);
		
		loadValues();

	}
	
//	boolean setupCameras() {
//		System.out.println("Getting number of cameras");
//// Checks available cameras
//		int numCams = CLCamera.cameraCount();
//		println("Found " + numCams + " cameras");
//		if (numCams == 0)
//			return false;
//// create cameras and start capture
//		for (int i = 0; i < numCams; i++) {
//// Prints Unique Identifier per camera
//			println("Camera " + (i + 1) + " UUID " + CLCamera.cameraUUID(i));
//// New camera instance per camera
//			myCameras[i] = new CLCamera(this);
//// ----------------------(i, CLEYE_GRAYSCALE/COLOR, CLEYE_QVGA/VGA, Framerate)
//			myCameras[i].createCamera(i, CLCamera.CLEYE_COLOR, CLCamera.CLEYE_VGA, cameraRate);
//// Starts camera captures
//			myCameras[i].startCamera();
//			myImages[i] = createImage(cameraWidth, cameraHeight, RGB);
//		}
//// resize the output window
//		size(cameraWidth * numCams, cameraHeight);
//		println("Complete Initializing Cameras");
//		return true;
//	}

	/**
	 * @return the matEditFunctionClasses
	 */
	public static Set<Class<? extends MatEditFunction>> getMatEditFunctionClasses() {
		if (matEditFunctionClasses == null)
			initReflectionClasses();
		return matEditFunctionClasses;
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
		parameterReceiverClasses=new HashSet<>();
		matStreamerClasses=new HashSet<>();
		
		Reflections reflections = new Reflections("functions.matedit");
		reflections.getSubTypesOf(MatEditFunction.class).forEach((clss)->{
			if(!Modifier.isAbstract(clss.getModifiers())) {
				matEditFunctionClasses.add(clss);
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

		menuBar = new MainMenuBar().getJMenuBar();
		setJMenuBar(menuBar);

		// setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
		setContentPane(contentPane);

		contentPane.setLayout(new BorderLayout(0, 0));

		mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);

		setVisible(true);
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

	/**
	 * @return the knownCameraResources
	 */
	public static Set<Object> getKnownCameraResources() {
		return knownCameraResources;
	}

	public static void initPreSetValues() {
		instance = new MainFrame();
	}

	@SuppressWarnings("unchecked")
	public static void loadValues() {
		try {
			Set<Object> output = (Set<Object>) Serializing.deSerialize(OftenUsedObjects.LIST_CAMERA_RESOURCES.getFile());
			if (output == null)
				throw new NullPointerException();
			knownCameraResources = output;
		} catch (Exception e) {
			e.printStackTrace();
			knownCameraResources = new HashSet<Object>();
		}
		
		try {
			diagramPanel.load(OftenUsedObjects.SESSION.getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveValues() {
		try {
//			for(VideoStreamer streamer:streamers) {
//				boolean contain=false;
//				for(Object res:knownCameraResources) {
//					if(streamer.getResource().equals(res)) {
//						contain=true;
//						break;
//					}
//				}
//				if(!contain) {
//					knownCameraResources.add(streamer.getResource());
//				}
//			}
			Serializing.serialize((Serializable) knownCameraResources, OftenUsedObjects.LIST_CAMERA_RESOURCES.getFile());
		}catch (Exception e) {
			e.printStackTrace();
		}
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
