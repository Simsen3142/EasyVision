package main;

import java.awt.BorderLayout;
import java.io.File;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIDefaults;

import org.opencv.core.Core;
import org.reflections.Reflections;

import cvfunctions.LineDetection_x;
import cvfunctions.MatEditFunction;
import database.OftenUsedObjects;
import database.Serializing;
import diagramming.DiagramPanel;
import main.menu.MainMenuBar;
import recording.FileVideoStreamer;
import recording.VideoStreamer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import view.MatReceiverPanel;
import view.MatEditFunctionMatsPanel;
import view.PanelFrame;

import javax.swing.UIManager;
import javax.swing.JMenuBar;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame implements Serializable {

	private JPanel contentPane;
	private static List<MatEditFunction> functions;
	private JPanel mainPanel;
	private static Set<String> knownCameraIps;
	private transient JMenuBar menuBar;
	private ThisWindowListener windowListener;

	private static MainFrame instance;
	private static List<VideoStreamer> streamers = new ArrayList<>();
	private static Set<Class<? extends MatEditFunction>> matEditFunctionClasses;

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

		String opencvpath = System.getProperty("user.dir") + "/opencv/build/java/x64/";
		System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
		System.load(System.getProperty("user.dir") + "/opencv/build/x64/vc15/bin/opencv_ffmpeg341_64.dll");

		initMatEditFunctionClasses();
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
		
		
        VideoStreamer videoStreamer=new VideoStreamer(0);
        streamers.add(videoStreamer);
//		videoStreamer.addMatReceiver(functions.get(0));
		
        FileVideoStreamer videoStreamer_video=new FileVideoStreamer(new File("test/SampleVideo_1280x720_1mb.mp4"));
        streamers.add(videoStreamer_video);
        videoStreamer_video.addMatReceiver(functions.get(0));

		instance.setContentPanel(new MatReceiverPanel(functions.get(0)));

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
//					functions.get(0).showParameterChangeDialog();
//					videoStreamer.showParameterChangeDialog();
//
//					
//					MatEditFunctionMatsPanel pnl=new MatEditFunctionMatsPanel(functions.get(0), "line");
//					new PanelFrame(pnl,"line").setVisible(true);
//					
//					MatEditFunctionMatsPanel pnl1=new MatEditFunctionMatsPanel(functions.get(0), "squares");
//					new PanelFrame(pnl1,"squares").setVisible(true);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				DiagramPanel panel = new DiagramPanel();
				new PanelFrame(panel).setVisible(true);
			}
		}).start();

		saveValues();
	}

	/**
	 * @return the matEditFunctionClasses
	 */
	public static Set<Class<? extends MatEditFunction>> getMatEditFunctionClasses() {
		if (matEditFunctionClasses == null)
			initMatEditFunctionClasses();
		return matEditFunctionClasses;
	}

	/**
	 * @return the streamers
	 */
	public static List<VideoStreamer> getStreamers() {
		return streamers;
	}

	private static void initMatEditFunctionClasses() {
		Reflections reflections = new Reflections();
		matEditFunctionClasses = reflections.getSubTypesOf(MatEditFunction.class);
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

		contentPane.setVisible(false);
		contentPane.setVisible(true);
	}

	/**
	 * @return the functions
	 */
	public static List<MatEditFunction> getFunctions() {
		return functions;
	}

	/**
	 * @return the knownCameraIps
	 */
	public static Set<String> getKnownCameraIps() {
		return knownCameraIps;
	}

	public static void initPreSetValues() {
		knownCameraIps = new HashSet<String>();
		knownCameraIps.add("http://192.168.43.71:8080/video?x.mjpeg");
		functions = new ArrayList<>();
		functions.add(new LineDetection_x());
		instance = new MainFrame();
	}

	@SuppressWarnings("unchecked")
	public static void loadPreSetValues() {
		try {
			Set<String> output = (Set<String>) Serializing.deSerialize(OftenUsedObjects.LIST_IP_CAMERA.getFile());
			if (output == null)
				throw new NullPointerException();
			knownCameraIps = output;
		} catch (Exception e) {
			e.printStackTrace();
			knownCameraIps = new HashSet<String>();
//			knownCameraIps.add("http://192.168.1.46:8080/video?x.mjpeg");
		}

		try {
			List<MatEditFunction> output = (List<MatEditFunction>) Serializing
					.deSerialize(OftenUsedObjects.LIST_MATEDITFUNCTIONS.getFile());
			if (output == null)
				throw new NullPointerException();
			functions = output;
		} catch (Exception e) {
			e.printStackTrace();
			functions = new ArrayList<>();
			functions.add(new LineDetection_x());
		}

		try {
			MainFrame output = (MainFrame) Serializing.deSerialize(OftenUsedObjects.MAIN_FUNCTION.getFile());
			if (output == null)
				throw new NullPointerException();
			instance = output;
		} catch (Exception e) {
			e.printStackTrace();
			instance = new MainFrame();
		}
	}

	public static void saveValues() {
		Serializing.serialize((Serializable) knownCameraIps, OftenUsedObjects.LIST_IP_CAMERA.getFile());
		Serializing.serialize((Serializable) functions, OftenUsedObjects.LIST_MATEDITFUNCTIONS.getFile());
		Serializing.serialize((Serializable) instance, OftenUsedObjects.MAIN_FUNCTION.getFile());
	}

	private static class ThisWindowListener extends WindowAdapter implements Serializable {
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("SAVING...");
			saveValues();
			System.exit(0);
		}
	}
}
