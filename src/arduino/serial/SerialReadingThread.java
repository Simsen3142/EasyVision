package arduino.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import gnu.io.SerialPort;

public class SerialReadingThread extends Thread {
		private BufferedReader reader;
		private List<Function<String,Void>> onReceives;

		public SerialReadingThread(InputStream in) {
			reader=new BufferedReader(new InputStreamReader(in));
		}
		
		/**
		 * @param onReceives the onReceives to set
		 */
		public void setOnReceives(List<Function<String, Void>> onReceives) {
			this.onReceives = onReceives;
		}

		public SerialReadingThread(SerialPort port) throws IOException {
			this(port.getInputStream());
		}

		public void run() {
				String line="";
				while (!this.isInterrupted()) {
					try {
						try {
							if(reader!=null)
								line = reader.readLine();
			                if (line == null || line.isEmpty()) {
			                    Thread.sleep(50);
			                    continue;
			                }
						}catch (IOException e) {
		                    Thread.sleep(50);
		                    continue;
						}
						System.out.println(line);
						handleLine(line);
						line="";
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			
		}
		
		private void handleLine(String line) {
			invokeOnReceives(line);
		}

		public void addOnReceive(Function<String,Void> onReceive) {
			if(onReceives==null)
				onReceives=new ArrayList<>();
			onReceives.add(onReceive);
		}

		public void removeOnReceive(Function<String,Void> onReceive) {
			onReceives.remove(onReceive);
		}
		
		private void invokeOnReceives(String line) {
			if(onReceives!=null) {
				for(Function<String,Void> onReceives:onReceives) {
					onReceives.apply(line);
				}
			}
		}
	}