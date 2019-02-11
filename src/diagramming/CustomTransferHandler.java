package diagramming;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;


public class CustomTransferHandler extends TransferHandler {
	private static String _text = "";
	private static boolean _boxCreated = false;

	private static final long serialVersionUID = 1L;
	
	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		return new StringSelection(((JList<?>) c).getSelectedValue().toString());
	}

	@Override
	protected void exportDone(JComponent c, Transferable t, int action) {

	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (canImport(c, t.getTransferDataFlavors())) {
			// Can only drop on the flow chart control
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					_text = (String) t.getTransferData(DataFlavor.stringFlavor);
					_boxCreated = true;
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (DataFlavor.stringFlavor.equals(flavors[i]))
				return true;
		}

		return false;
	}

	public static boolean isBoxCreated() {
		return _boxCreated;
	}

	public static void setBoxCreate(boolean is) {
		_boxCreated = is;
	}

	public static String getText() {
		return _text;
	}

	public static void cleanText() {
		_text = "";
	}
}