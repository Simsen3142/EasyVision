package functions.matedit.multi;

import java.awt.Image;
import java.util.List;

import database.ImageHandler;

public class LogicalXOR extends LogicalFunctions{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6789241306630476342L;
	
	public LogicalXOR(Boolean empty) {
	}
	
	public LogicalXOR() {
		super();
	}

	@Override
	protected boolean performLogic(List<Boolean> bs) {
		boolean found=false;
		for(boolean b:bs) {
			if(b) {
				if(!found) {
					found=true;
				}else {
					return false;
				}
			}
		}
		return found;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/xor.png");
	}
}
