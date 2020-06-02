package communication.netsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.fazecast.jSerialComm.SerialPort;

import communication.ReadingThread;
import communication.TwoWayConnection;
import communication.Writer;

public class SocketComm extends TwoWayConnection<InetSocketAddress> {
	private ReadingThread reader;
	private Writer writer;
	private Socket socket;
	
	/**
	 * @return the reader
	 */
	@Override
	public void startReader() {
		if (reader!=null && reader.isAlive())
			return;
		
		reader=createReader();
		reader.start();
	}
	
	public Socket getSocket() {
		return socket;
	}

	public ReadingThread createReader() {
		ReadingThread reader=null;
		try {
			System.out.println(socket.getInputStream());
			reader = new ReadingThread(socket.getInputStream());
			List<Function<String, Void>> onReceives = new ArrayList<Function<String, Void>>();
			onReceives.add((text) -> {
				this.triggerOnReceives(text);
				return null;
			});
			reader.setOnReceives(onReceives);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reader;
	}

	/**
	 * @param reader the reader to set
	 */
	public void stopReader() {
		if (reader == null || !reader.isAlive()) {
			return;
		}
		reader.interrupt();
	}

	public SocketComm() {
		super();
	}
	
	private Writer getWriter() {
		if (writer == null) {
			if (socket != null) {
				try {
					System.out.println("INIT WRITER");
					writer = new Writer(socket.getOutputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return writer;
	}

	@Override
	public boolean disconnect() {
		boolean ret=false;
		stopReader();
		reader = null;
		writer = null;
		if (socket != null) {
			try {
				socket.close();
				ret=true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		socket = null;
		return ret;
	}

	@Override
	public boolean sendText(String text) {
		try {
			getWriter().doWrite(text);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected boolean connect(InetSocketAddress endpoint) {
		try {
			if(isConnected()) {
				if((!socket.getInetAddress().equals(endpoint.getAddress())&& socket.getPort()!=endpoint.getPort())) {
					disconnect();
				}else {
					return true;
				}
			}
			socket=new Socket(endpoint.getAddress(), endpoint.getPort());
			startReader();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean isConnected() {
		return socket!=null;
	}

	@Override
	public InputStream getInputStream() {
		if(isConnected())
			try {
				return socket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	@Override
	public OutputStream getOutputStream() {
		if(isConnected())
			try {
				return socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}		return null;
	}
}
