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
		System.out.println("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		writer.println(text);
		System.out.println("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
		writer.flush();
		System.out.println("2222222222221111111111111111111111111111111122222222222222222222222222222222222222222222");
	}
	
	public void doWrite(String text) {
		System.out.println("WRITE ?");
		new Thread(()->{
			doWriteSynch(text);
		}).start();
	}
	
}