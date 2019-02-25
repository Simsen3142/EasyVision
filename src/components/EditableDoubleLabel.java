package components;

public class EditableDoubleLabel extends EditableLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 425251L;

	public EditableDoubleLabel(String text) {
		super(text);
	}
	
	@Override
	protected String handleText(String text, String alternativeText) {
		try {
			double x=Double.parseDouble(text);
			return x+"";
		}catch (Exception e) {
			return alternativeText;
		}
	}
}
