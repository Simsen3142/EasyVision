package parameters;

import javax.swing.JComponent;

import org.opencv.core.Mat;

public class MatParameter extends Parameter<Mat> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2357569810935470375L;

	public MatParameter(String name, Mat value) {
		super(name, value);
	}
	
	public MatParameter(String name, Mat value, boolean editable) {
		super(name, value, editable);
	}

	@Override
	public JComponent getEditComponent() {
		// TODO Auto-generated method stub
		return null;
	}
}
