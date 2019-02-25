package diagramming.components;

import javax.swing.JLabel;
import functions.matedit.MatEditFunction;

public class MatEditFunctionDiagramPanel extends MatReceiverNSenderPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3892310953992021206L;

	/**
	 * Create the panel.
	 */
	public MatEditFunctionDiagramPanel(MatEditFunction function) {
		super(function,function.getClass().getSimpleName());
	}
}
