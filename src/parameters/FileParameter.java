package parameters;

import java.io.File;

import javax.swing.JComponent;

import parameters.components.ParameterFileChoosePanel;

public class FileParameter extends Parameter<File> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2941169802455547488L;

	public FileParameter(String name, File value) {
		super(name, value);
	}
	
	public FileParameter(String name, File value, boolean editable) {
		super(name, value, editable);
	}

	@Override
	public JComponent getEditComponent() {
		return new ParameterFileChoosePanel(this);
	}
}
