package diagramming.components;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import cvfunctions.MatEditFunction;
import recording.VideoStreamer;

public class StreamerPanel extends MatReceiverNSenderPanel {

	/**
	 * Create the panel.
	 */
	public StreamerPanel(VideoStreamer streamer) {
		super(streamer,streamer.toString());
	}
}
