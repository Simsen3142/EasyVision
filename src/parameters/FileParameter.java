package parameters;

import java.io.File;

import javax.swing.JComponent;

import parameters.components.ParameterBooleanCheckboxPanel;
import parameters.components.ParameterFileChoosePanel;

public class FileParameter extends Parameter<File> {

	public FileParameter(String name, File value) {
		super(name, value);
	}

	@Override
	public JComponent getComponent() {
		return new ParameterFileChoosePanel(this);
	}
}
