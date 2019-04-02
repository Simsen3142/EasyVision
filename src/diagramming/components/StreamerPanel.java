package diagramming.components;

import functions.streamer.MatStreamer;

public class StreamerPanel extends MatReceiverNSenderPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 746103210410662185L;

	/**
	 * Create the panel.
	 */
	public StreamerPanel(MatStreamer streamer) {
		super(streamer,streamer.getClass().getSimpleName());
	}
}
