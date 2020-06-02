package parameters;

import java.util.Collection;

import javax.swing.JComponent;

import parameters.components.ParameterCollectionPanel;

public class CollectionParameter extends Parameter<Collection<?>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4317877211296000993L;

	public CollectionParameter(String name, Collection<?> value) {
		super(name, value, false);
	}
	
	@Override
	public JComponent getEditComponent() {
		return new ParameterCollectionPanel(this);
	}
}
