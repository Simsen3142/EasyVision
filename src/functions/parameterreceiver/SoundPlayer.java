package functions.parameterreceiver;

import java.io.File;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.google.common.reflect.Parameter;

import diagramming.components.ParameterReceiverPanel;
import main.ParameterReceiver;
import parameters.FileParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class SoundPlayer extends ParameterizedObject implements ParameterReceiver {
	private int id=System.identityHashCode(this);


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
	
	public SoundPlayer() {
		super(new FileParameter("soundfile", new File("sounds/alarm1.wav")));
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters) {
		for(String paramname :parameters.keySet()) {
			if(paramname.equals("playsound")) {
				playSound(getFileVal("soundfile"));
			}
		}
	}
	
	public static synchronized void playSound(File file) {
		new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing,
									// see comments
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(file.getAbsoluteFile());
					clip.open(inputStream);
					clip.start();
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
}
