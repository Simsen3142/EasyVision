package parameters;

import java.util.List;

import javax.swing.JComponent;

import parameters.components.ParameterStringMultiOptionPanel;
import parameters.components.ParameterStringPanel;

public class StringMultiOptionParameter extends StringParameter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8578501229811357067L;
	private List<String> options;
	
	public List<String> getOptions(){
		return this.options;
	}
	
	public void setOptions(List<String> options){
		this.options=options;
	}

	public StringMultiOptionParameter(String name, String value) {
		super(name, value);
	}
	
	public StringMultiOptionParameter(String name, String value, boolean editable) {
		super(name, value, editable);
	}
	
	public StringMultiOptionParameter(String name, String value, int minLength, int maxLength) {
		super(name, value, minLength, maxLength);
	}
	


	@Override
	public JComponent getEditComponent() {
		return new ParameterStringMultiOptionPanel(this);
	}
}
