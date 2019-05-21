package functions.parameterreceiver;

import java.awt.Image;
import java.io.File;
import java.util.Map;
import java.util.function.Function;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import com.google.common.reflect.Parameter;

import database.ImageHandler;
import diagramming.components.ParameterReceiverPanel;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.FileParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class SoundPlayer extends ParameterizedObject implements ParameterReceiver, RepresentationIcon {
	private int id=System.identityHashCode(this);
	
	private transient volatile boolean alreadyPlaying=false;


	@Override
	public void recalculateId() {
		this.id*=Math.random();
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public SoundPlayer(Boolean empty) {
		
	}
	
	public SoundPlayer() {
		super(new FileParameter("soundfile", new File("sounds/alarm1.wav")));
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		Boolean b=getFirstFittingParameter(parameters, BooleanParameter.class).getValue();
		
		if(b!=null && b) {
			if(!alreadyPlaying) {
				Object lock=new Object();
				alreadyPlaying=true;
				
				playSound(getFileVal("soundfile"),lock);
				
				new Thread(()-> {
					try {
						System.out.println("WAITING...");
						synchronized(lock) {
							lock.wait();
						}
					} catch (InterruptedException e) {
					}
					alreadyPlaying=false;
				}).start();
			}
		}
	}
	
	public static synchronized void playSound(File file, Object lock) {
		new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing,
									// see comments
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(file.getAbsoluteFile());
					clip.open(inputStream);
					clip.start();
					clip.addLineListener(new LineListener() {
						
						@Override
						public void update(LineEvent event) {
							System.out.println("XI: "+event.getType().equals(Type.STOP));
							if(event.getType().equals(Type.STOP)) {
								synchronized(lock) {
									lock.notifyAll();
								}
							}
						}
					});
					
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SoundPlayer))
			return false;
		SoundPlayer other = (SoundPlayer) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/speaker.png");
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}
}
