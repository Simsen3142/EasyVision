package listener;

import java.awt.Component;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ListenerHandler {
	public static void copyListeners(Component from,Component to) {
		for(MouseListener lstnr:from.getMouseListeners()) {
			to.addMouseListener(lstnr);
		}
		for(MouseMotionListener lstnr:from.getMouseMotionListeners()) {
			to.addMouseMotionListener(lstnr);
		}
		for(KeyListener lstnr:from.getKeyListeners()) {
			to.addKeyListener(lstnr);
		}	
		for(FocusListener lstnr:from.getFocusListeners()) {
			to.addFocusListener(lstnr);
		}	
	}
	
	public static void clearListeners(Component component) {
		for(MouseListener lstnr:component.getMouseListeners()) {
			component.removeMouseListener(lstnr);
		}
		for(MouseMotionListener lstnr:component.getMouseMotionListeners()) {
			component.removeMouseMotionListener(lstnr);
		}
		for(KeyListener lstnr:component.getKeyListeners()) {
			component.removeKeyListener(lstnr);
		}	
		for(FocusListener lstnr:component.getFocusListeners()) {
			component.removeFocusListener(lstnr);
		}	
	}
}
