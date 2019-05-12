package arduino.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import gnu.io.SerialPort;

public class SerialWriter {
	private OutputStream os;
	private PrintWriter writer;

	public SerialWriter(OutputStream os) {
		this.os = os;
		writer=new PrintWriter(os);
	}

	public SerialWriter(SerialPort port) throws IOException {
		this(port.getOutputStream());
	}

	private synchronized void doWriteSynch(String text) {
		System.out.println("WRITING: "+text);
		writer.println(text);
		writer.flush();
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void doWrite(String text) {
		new Thread(()->{
			doWriteSynch(text);
		}).start();
	}
	
}