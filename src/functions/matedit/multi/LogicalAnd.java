package functions.matedit.multi;

import java.awt.Image;
import java.util.List;

import database.ImageHandler;

public class LogicalAnd extends LogicalFunctions{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6789241306630476342L;

	public LogicalAnd(Boolean empty) {
	}
	
	@Override
	protected boolean performLogic(List<Boolean> bs) {
		
		for(boolean b:bs) {
			if(!b) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/and.png");
	}
}
