package functions.matedit;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import javax.swing.ImageIcon;

import org.opencv.core.Mat;

import functions.Startable;
import parameters.IntegerParameter;
import view.MatPanel;

public class MatSocketStreamer extends MatEditFunction implements Startable {
	
	private volatile transient ServerSocket server;
	private volatile transient ClientForHost client;
	private transient Thread streamThread;
	private volatile transient Mat lastMat;

	public MatSocketStreamer() {
		super(new IntegerParameter("portnr", 3142));
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		lastMat=matIn;
		return matIn;
	}

	@Override
	public void start() {
		new Thread(()->{
			try {
				server=new ServerSocket(getIntVal("portnr"));
				client = new ClientForHost(server.accept());
				
				streamThread=createStreamThread();
				streamThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	private Thread createStreamThread() {
		return 
			new Thread() {
				@Override public void run() {
					while(!isInterrupted()) {
						if(server!=null && server.isBound() && client!= null&& client.isBound()) {
							if(lastMat!=null){
								BufferedImage ic=MatPanel.matToBufferedImage(lastMat);
								client.sendObject(new ImageIcon(ic));
							}
						}
					}
				}
			};
	}
	
	@Override
	public void stop() {
		try {
			if(client!=null)
				client.close();
			if(server!=null)
				server.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(streamThread!=null)
			streamThread.interrupt();
			
		streamThread=null;
		client=null;
		server=null;
	}

	@Override
	public boolean isStarted() {
		return server!=null;
	}
	
	
	public class ClientForHost {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private ObjectOutputStream outputO;
        private String clientName;

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public ClientForHost(Socket socket) {
            this.socket = socket;
            clientName = socket.getLocalAddress() + "";
            try {
                this.input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                this.output = new PrintWriter(socket.getOutputStream(), true);
                this.outputO = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
            }
        }
        
        public void sendObject(Object o) {
        	if(outputO!=null) {
	        	try {
					outputO.writeObject(o);
					outputO.flush();
					outputO.reset();
	        	} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }

        public Socket getSocket() {
            return socket;
        }
        
        public boolean isBound() {
        	return socket!=null && socket.isBound();
        }
        
        public void close() {
        	try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
	

}
