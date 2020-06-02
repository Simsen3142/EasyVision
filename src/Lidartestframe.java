import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
//import javax.xml.bind.DatatypeConverter;

import org.apache.pdfbox.util.Hex;

import communication.netsocket.NetworkHandler;
import communication.netsocket.SocketComm;
import net.miginfocom.swing.MigLayout;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class Lidartestframe extends JFrame {

	private JPanel contentPane;
	private static int port=80;
	private static String ip="192.168.4.1";
	private JTextArea txtrSend;
	private JTextArea txtrReceive;
	private SocketComm com;
	private JScrollPane scrollPane_1;
	private JTextArea txtrSent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Lidartestframe frame = new Lidartestframe();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		InetSocketAddress addr=new InetSocketAddress(ip, port);
		
		NetworkHandler.getInstance().connectTo(addr);
		frame.com=NetworkHandler.getInstance().getCommunication(addr);
		frame.com.addOnReceive((txt)->{
			String txt2="";
			for(byte b:txt.getBytes()) {
				txt2+=Hex.getString(b)+" ";
			}
			txt2+="\n";
			System.out.println("RECEIVED: "+txt2);
			frame.txtrReceive.append(txt2);
			frame.txtrReceive.revalidate();
			frame.txtrReceive.repaint();
			return null;
		});
	}

	/**
	 * Create the frame.
	 */
	public Lidartestframe() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][]", "[grow][grow][grow]"));
		
		txtrSend = new JTextArea();
		contentPane.add(txtrSend, "cell 0 0,grow");
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new BtnSendActionListener());
		contentPane.add(btnSend, "cell 1 0");
		
		scrollPane_1 = new JScrollPane();
		contentPane.add(scrollPane_1, "cell 0 1 2 1,grow");
		
		txtrSent = new JTextArea();
		txtrSent.setEditable(false);
		scrollPane_1.setViewportView(txtrSent);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "cell 0 2 2 1,grow");
		
		txtrReceive = new JTextArea();
		txtrReceive.setFont(new Font("Arial", Font.PLAIN, 13));
		txtrReceive.setEditable(false);
		scrollPane.setViewportView(txtrReceive);
	}

	private class BtnSendActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String text=txtrSend.getText();

			txtrSent.append("> ");
			
//			com.sendText(text);
//			txtrSent.append(text);

			byte[] bs=null;//DatatypeConverter.parseHexBinary(text);
			try {
				com.getOutputStream().write(bs);
				com.getOutputStream().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			for(byte b:bs) {
				txtrSent.append(Hex.getString(b)+" ");
			}
			
			
			txtrSent.append("\n");
			txtrSent.revalidate();
			txtrSent.repaint();
		}
	}
}
