package parameters;

import javax.swing.JComponent;

import org.opencv.core.Mat;

public class MatParameter extends Parameter<Mat> {
	public MatParameter(String name, Mat value) {
		super(name, value);
	}

	@Override
	public JComponent getComponent() {
		// TODO Auto-generated method stub
		return null;
	}
}
