package functions.parameterreceiver;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.pdfbox.util.Hex;

import communication.TwoWayConnection;
import communication.netsocket.NetworkHandler;
import communication.serial.SerialHandler;
import communication.serial.TwoWaySerialComm;
import functions.RepresentationIcon;
import functions.Startable;
import main.ParameterReceiver;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.SerialPortParameter;
import parameters.group.ConnectionParameterGroup;
import parameters.group.ParameterGroup;
import sensors.lidar.LidarCommand;
import sensors.lidar.LidarHealthResponse;
import sensors.lidar.LidarInfoResponse;
import sensors.lidar.LidarMessageSender;
import sensors.lidar.LidarResponse;
import sensors.lidar.LidarSamplerateResponse;

public abstract class SuperLidarHandler extends ParameterizedObject
		implements ParameterReceiver, RepresentationIcon, Startable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1535611369117870650L;
	private int id = System.identityHashCode(this);
	private transient TwoWayConnection<?> lidarCon;
	private transient Thread streamThread;
	private static Map<TwoWayConnection<?>, Boolean> motorStarted;
	private static Map<TwoWayConnection<?>, Boolean> scanning;

	public static final byte SYNC_BYTE0 = (byte) 0xA5;
	public static final byte SYNC_BYTE1 = (byte) 0x5A;

	@Override
	public void recalculateId() {
		this.id *= Math.random();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public SuperLidarHandler(Boolean empty) {

	}

	public SuperLidarHandler(ParameterObject... outputs) {
		super(new ConnectionParameterGroup("connection"), new ParameterGroup("output"));
		((ParameterGroup) getParameter2("output")).addParameters(outputs);
		addParameters(outputs);
	}

	protected TwoWayConnection<?> getConnection() {
		return getConnectionParameter().getConnection();
	}

	protected ConnectionParameterGroup getConnectionParameter() {
		return ((ConnectionParameterGroup) getParameter2("connection"));
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters, ParameterizedObject sender) {
	}

	private static Map<TwoWayConnection<?>, Boolean> getMotorStarted() {
		if (motorStarted == null)
			motorStarted = new HashMap<TwoWayConnection<?>, Boolean>();
		return motorStarted;
	}

	private static Map<TwoWayConnection<?>, Boolean> getScanning() {
		if (scanning == null)
			scanning = new HashMap<TwoWayConnection<?>, Boolean>();
		return scanning;
	}

	public boolean isMotorStarted() {
		if (getMotorStarted().get(getConnection()) == null)
			return false;
		return getMotorStarted().get(getConnection());
	}

	public boolean isScanning() {
		if (getScanning().get(getConnection()) == null)
			return false;
		return getScanning().get(getConnection());
	}

	public void startMotor() {
		TwoWayConnection<?> comm = getConnection();
		if (comm instanceof TwoWaySerialComm && comm.isConnected()) {
			((TwoWaySerialComm) comm).getPort().clearDTR();
			getMotorStarted().put(comm, true);
		}
	}

	public void stopMotor() {
		TwoWayConnection<?> comm = getConnection();
		if (comm instanceof TwoWaySerialComm && comm.isConnected()) {
			((TwoWaySerialComm) comm).getPort().setDTR();
			getMotorStarted().put(comm, false);
		}
	}

	public void startForceScan() {
		sendNoPayload(LidarCommand.FORCE_SCAN);
		getScanning().put(getConnection(), true);
	}

	public void startScan() {
		sendNoPayload(LidarCommand.SCAN);
		getScanning().put(getConnection(), true);
	}

	public void reset() {
		sendNoPayload(LidarCommand.RESET);
	}

	public void stopLidar() {
		sendNoPayload(LidarCommand.STOP);
		getScanning().put(getConnection(), false);
	}

	public LidarHealthResponse getDeviceHealth() {
		boolean scanned = false;
		try {
			if (scanned = isScanning()) {
				if (getConnection() != null && getConnection() instanceof LidarMessageSender) {
					getConnection().stopReader();
				}
				stopLidar();
			}
			return (LidarHealthResponse) sendNoPayload(LidarCommand.GET_HEALTH);
		} finally {
			if (scanned) {
				if (getConnection() != null && getConnection() instanceof LidarMessageSender) {
					getConnection().startReader();
				}
				startScan();
			}
		}
	}

	public LidarInfoResponse getDeviceInfo() {
		boolean scanned = false;
		try {
			if (scanned = isScanning()) {
				if (getConnection() != null && getConnection() instanceof LidarMessageSender) {
					getConnection().stopReader();
				}
				stopLidar();
			}
			return (LidarInfoResponse) sendNoPayload(LidarCommand.GET_INFO);
		} finally {
			if (scanned) {
				if (getConnection() != null && getConnection() instanceof LidarMessageSender) {
					getConnection().startReader();
				}
				startScan();
			}
		}
	}

	public LidarSamplerateResponse getSampleRate() {
		boolean scanned = false;
		try {
			if (scanned = isScanning()) {
				if (getConnection() != null && getConnection() instanceof LidarMessageSender) {
					getConnection().stopReader();
				}
				stopLidar();
			}
			return (LidarSamplerateResponse) sendNoPayload(LidarCommand.GET_SAMPLERATE);
		} finally {
			if (scanned) {
				if (getConnection() != null && getConnection() instanceof LidarMessageSender) {
					getConnection().startReader();
				}
				startScan();
			}
		}
	}

	protected LidarResponse sendNoPayload(LidarCommand command) {
		TwoWayConnection<?> comm = getConnection();
		if (comm != null && comm.isConnected()) {
			byte[] dataOut = new byte[1024];
			dataOut[0] = SYNC_BYTE0;
			dataOut[1] = command.B;
			try {
//				connectedPort.getInputStream().skip(connectedPort.getInputStream().available());
				{
					int available = comm.getInputStream().available();
					comm.getInputStream().read(new byte[available], 0, available);
				}
				comm.getOutputStream().write(dataOut, 0, 2);
				comm.getOutputStream().flush();
				System.out.println("SENDING: " + Hex.getString(SYNC_BYTE0) + " " + Hex.getString(command.B));
			} catch (IOException e1) {
				//TODO: disconnected logic
				e1.printStackTrace();
			}

			boolean sleep = false;
			LidarResponse resp = null;

			switch (command) {
			case GET_HEALTH:
				resp = new LidarHealthResponse();
				break;
			case GET_INFO:
				resp = new LidarInfoResponse();
				break;
			case GET_SAMPLERATE:
				resp = new LidarSamplerateResponse();
				break;
			case RESET:
			case STOP:
				sleep = true;
				break;
			default:
				break;
			}

			if (sleep) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			}

			if (resp != null) {
				byte[] data = response(1000, resp);
				if (data != null) {
					resp.parseData(data);
				}
				return resp;
			}
		}
		return null;
	}

	public byte[] response(int timeout, LidarResponse resp) {
		byte[] data = new byte[1024];
		byte[] dataIn = new byte[1024];
		final int defBits = 7;
		int dataRead = 0;
		int offset = 0;
		int datalength = 0;
		byte[] dataOut = new byte[resp.getLength()];
		long t0 = System.currentTimeMillis();

		TwoWayConnection<?> comm = getConnection();
		InputStream is = comm.getInputStream();
		try {
			// Make sure we dont run longer than specified timeout
			while (System.currentTimeMillis() - t0 < timeout) {
				// Do we have enough bytes?
				if (is.available() + (dataRead - offset) >= resp.getLength() + defBits) {
					System.arraycopy(data, offset, data, 0, dataRead - offset);
					dataRead = is.read(dataIn, 0, resp.getLength() + defBits);
					datalength = dataRead + offset;
					System.arraycopy(dataIn, 0, data, offset, dataRead);
					offset = 0;

					while (datalength - offset >= resp.getLength() + defBits) {
						if ((data[offset] == SYNC_BYTE0) && (data[offset + 1] == SYNC_BYTE1)) {
							System.arraycopy(data, offset + defBits, dataOut, 0, dataOut.length);
							for (byte b : dataOut) {
								System.out.print(Hex.getString(b) + " ");
							}
							System.out.println(" -> dataOut");

							return dataOut;
						}
						offset++;
					}
				} else {
					Thread.sleep(10);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SuperLidarHandler))
			return false;
		SuperLidarHandler other = (SuperLidarHandler) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Image getRepresentationImage() {
		return null;
	}

	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(() -> {
			Image img = getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}

	protected abstract Thread initStreamThread();

	protected Thread getStreamThread() {
		return streamThread;
	}

	@Override
	public void start() {
		ConnectionParameterGroup conparam = getConnectionParameter();
		if (lidarCon == null || !lidarCon.isConnected()) {
			TwoWayConnection<?> conn = getConnection();
			if (conn != null && conn instanceof LidarMessageSender) {
				lidarCon = conn;
			} else {
				if (conn != null) {
					conn.disconnect();
				}
				switch (conparam.getConnectionType()) {
				case BLUETOOTH:
					break;
				case NETWORK:
					NetworkHandler.getInstance().connectTo(conparam.getInetSocketAddress(), 1);
					break;
				case SERIAL:
					SerialHandler.getInstance().connectTo(conparam.getSelectedSerialPort(), 1);
					break;
				default:
					break;
				}
				lidarCon = conparam.getConnection();
			}
		}
		if (lidarCon != null) {
			if (streamThread == null || !streamThread.isAlive() || streamThread.isInterrupted()) {
				streamThread = initStreamThread();
				streamThread.start();
			}
		}
	}

	@Override
	public void stop() {
		if (streamThread != null)
			streamThread.interrupt();
		super.stop();
	}

	@Override
	public boolean isStarted() {
		return streamThread != null && streamThread.isAlive();
	}
}
