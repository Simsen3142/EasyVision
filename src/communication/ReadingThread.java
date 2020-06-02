package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import gnu.io.SerialPort;

public class ReadingThread extends Thread {
	private BufferedReader reader;
	private List<Function<String, Void>> onReceives;
	private transient InputStream in;

	public ReadingThread(InputStream in) {
		this.in = in;
		reader = new BufferedReader(new InputStreamReader(in));
	}
	
	protected InputStream getIn() {
		return in;
	}

	/**
	 * @param onReceives the onReceives to set
	 */
	public void setOnReceives(List<Function<String, Void>> onReceives) {
		this.onReceives = onReceives;
	}

	public ReadingThread(SerialPort port) throws IOException {
		this(port.getInputStream());
	}

	public void run() {
		String line = "";
		while (!this.isInterrupted()) {
			try {
				try {
					if (reader != null) {
						int available=in.available();
						if (available > 0) {
							char[] cs=new char[available];
							reader.read(cs, 0, available);
							for(char c:cs) {
								line+=c;
							}
							Thread.sleep(50);
						}
					}
					if (line == null || line.isEmpty()) {
						Thread.sleep(50);
						continue;
					}
					Thread.sleep(50);
				} catch (IOException e) {
					Thread.sleep(50);
					e.printStackTrace();
					continue;
				}
				handleLine(line);
				line = "";
			} catch (Exception e) {
				this.interrupt();
			}
		}
	}

	static long millis = 0;
	static long counter = 0;
	static long counter1 = 0;
	static long startTime = System.currentTimeMillis();

	private void handleLine(String line) {
		invokeOnReceives(line);
	}

	public void addOnReceive(Function<String, Void> onReceive) {
		if (onReceives == null)
			onReceives = new ArrayList<>();

		onReceives.add(onReceive);
	}

	public void removeOnReceive(Function<String, Void> onReceive) {
		onReceives.remove(onReceive);
	}

	private void invokeOnReceives(String line) {
		if (onReceives != null) {
			for (Function<String, Void> onReceive : onReceives) {
				onReceive.apply(line);
			}
		}
	}
}