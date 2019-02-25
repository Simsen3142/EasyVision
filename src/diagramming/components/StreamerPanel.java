package diagramming.components;

import functions.streamer.VideoStreamer;

public class StreamerPanel extends MatReceiverNSenderPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7461022580410662185L;

	/**
	 * Create the panel.
	 */
	public StreamerPanel(VideoStreamer streamer) {
		super(streamer,streamer.toString());
	}
}
