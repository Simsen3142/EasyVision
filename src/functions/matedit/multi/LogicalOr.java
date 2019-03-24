package functions.matedit.multi;

import java.awt.Image;
import java.util.List;

import database.ImageHandler;

public class LogicalOr extends LogicalFunctions{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1645535146515336445L;

	public LogicalOr(Boolean empty) {
	}
	
	@Override
	protected boolean performLogic(List<Boolean> bs) {
		for(boolean b:bs) {
			if(b) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/or.png");
	}
}
