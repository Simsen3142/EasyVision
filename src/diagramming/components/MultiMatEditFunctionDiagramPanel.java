package diagramming.components;

import javax.swing.JLabel;
import functions.matedit.MatEditFunction;
import functions.matedit.multi.MultiMatEditFunction;

public class MultiMatEditFunctionDiagramPanel extends MatReceiverNSenderPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -389233235649920206L;

	/**
	 * Create the panel.
	 */
	public MultiMatEditFunctionDiagramPanel(MultiMatEditFunction function) {
		super(function,function.getClass().getSimpleName());
	}
}
