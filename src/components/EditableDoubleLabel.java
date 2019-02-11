package components;

public class EditableDoubleLabel extends EditableLabel {
	private double x;

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
