package diagramming.components;

import functions.streamer.MatStreamer;

public class StreamerPanel extends MatReceiverNSenderPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7461022580410662185L;

	/**
	 * Create the panel.
	 */
	public StreamerPanel(MatStreamer streamer) {
		super(streamer,streamer.getClass().getSimpleName());
	}
}
