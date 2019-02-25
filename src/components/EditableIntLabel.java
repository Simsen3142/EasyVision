package components;

public class EditableIntLabel extends EditableLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 131337L;

	public EditableIntLabel(String text) {
		super(text);
	}
	
	@Override
	protected String handleText(String text, String alternativeText) {
		try {
			int x=Integer.parseInt(text);
			return x+"";
		}catch (Exception e) {
			return alternativeText;
		}
	}

}
