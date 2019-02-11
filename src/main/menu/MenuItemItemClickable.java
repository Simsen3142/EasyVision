package main.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public abstract class MenuItemItemClickable extends JMenuItem implements ItemClickable{

	private Object object;
	
	public MenuItemItemClickable(Object object, ActionListener al) {
		this.object=object;
		this.addActionListener(al);
		this.setText(this.getShownName());
	}
	
	@Override
	public Object getObject() {
		return object;
	}
}
