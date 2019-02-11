package diagramming;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ComponentCursorAdapter extends MouseAdapter {
	private boolean cursor=false;
    private KeyStroke keyStrokeP;
    private KeyStroke keyStrokeR;
    private JComponent jc;
    private Cursor cursorNormal;
    private Cursor cursorClicked;
    
    public ComponentCursorAdapter(int keyTyped, int inputMask, Cursor cursorNormal, Cursor cursorClicked) {
    	this.cursorNormal=cursorNormal;
    	this.cursorClicked=cursorClicked;
    	
    	keyStrokeP = KeyStroke.getKeyStroke(keyTyped, inputMask,false);
    	keyStrokeR = KeyStroke.getKeyStroke(keyTyped, 0,true);
    }
    
    public static void install(JComponent component, int keyTyped,  int inputMask, Cursor cursorNormal, Cursor cursorClicked) {
    	ComponentCursorAdapter cursorAdapter=new ComponentCursorAdapter(keyTyped, inputMask, cursorNormal, cursorClicked);
    	component.addMouseListener(cursorAdapter);
    }

	@Override
	public void mouseEntered(MouseEvent e) {
		jc=(JComponent)e.getSource();
		String clickedKey="clicked"+keyStrokeP.hashCode();
		String defaultKey="default"+keyStrokeR.hashCode();
		
		jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeP, clickedKey);
		jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeR, defaultKey);
		jc.getActionMap().put(clickedKey, actionSetHand);
		jc.getActionMap().put(defaultKey, actionSetDefault);
	}
	
	private Action actionSetHand = new AbstractAction() {
	    @Override
	    public void actionPerformed(ActionEvent ae) {
	    	if(!cursor){
	    		cursor=true;
	    		jc.setCursor(cursorClicked);
		    	jc.setOpaque(true);
		    	jc.repaint();
	    	}		

	    }
	};
	
	private Action actionSetDefault = new AbstractAction() {
	    @Override
	    public void actionPerformed(ActionEvent ae) {
	    	if(cursor){
		    	cursor=false;
		    	jc.setCursor(cursorNormal);
			}
	    }
	};

	@Override
	public void mouseExited(MouseEvent e) {
		if(cursor){
			cursor=false;
			jc.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeP, "none");
		jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeR, "none");
	}
}
