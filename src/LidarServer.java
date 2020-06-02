import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import com.fazecast.jSerialComm.SerialPort;

import communication.serial.SerialHandler;
import communication.serial.TwoWaySerialComm;

public class LidarServer {
	private static SerialPort port;
	private static Socket socket;
	
	public static void main(String[] args) {
		try {
			port=TwoWaySerialComm.getAvailablePorts().get(1);
			System.out.println(port);
			SerialHandler.getInstance().connectTo(port, 1);
			
			ServerSocket ssocket=new ServerSocket(8180);
			socket=ssocket.accept();
			
			System.out.println("STARTING");
			new LidarReadingThread().start();
			new SocketReadingThread().start();
			
			
			JFrame frame=new JFrame();
			frame.setBounds(0,0,100,100);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class LidarReadingThread extends Thread{
		private InputStream in;
		private OutputStream out;
		@Override
		public void run() {
			try {
				in=port.getInputStream();
				out=socket.getOutputStream();
				
				while(!this.isInterrupted()) {
					int amt=in.available();
					byte[] data=new byte[amt];
					in.read(data);
					
					out.write(data);
					out.flush();
					Thread.sleep(1);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class SocketReadingThread extends Thread{
		private InputStream in;
		private OutputStream out;
		@Override
		public void run() {
			try {
				in=socket.getInputStream();
				out=port.getOutputStream();
				
				while(!this.isInterrupted()) {
					int amt=in.available();
					byte[] data=new byte[amt];
					in.read(data);
					
					out.write(data);
					out.flush();
					Thread.sleep(1);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

}
