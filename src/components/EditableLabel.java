package components;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.*;
import java.util.function.Function;

public class EditableLabel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1425623L;
	private boolean editable=false;
	protected JLabel label;
	protected JTextField textField;
	protected String text;
	private Function<String[], String> onEdit=(txts)->{return txts[0];};
	public final static Font FONT=new Font("Calibri", Font.BOLD, 20);
	
	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}
	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable, Function<String[], String> onEdit) {
		setEditable(editable);
		this.onEdit=onEdit;
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(label!=null)
			label.setForeground(c);
	}

	/**
	 * Create the panel.
	 */
	public EditableLabel(String text) {
		this.text=text;
		initialize();
	}
	
	private void initialize() {
		addMouseListener(new ThisMouseListener());
		setOpaque(false);
		setLayout(new MigLayout("insets 0, gap 0", "[grow]", "[grow]"));
		
		label = new JLabel(text);
		label.setFont(FONT);
		setText(text);
		add(label, "cell 0 0,alignx center,growy");
		
		textField=new JTextField(text);
		textField.setFont(FONT);
		textField.addKeyListener(new TextFieldKeyListener());
		textField.addFocusListener(new TextFieldFocusListener());
		//add(textField, "cell 0 0,alignx center,growy");
	}

	private class ThisMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if(editable) {
				remove(label);
				add(textField, "cell 0 0,grow");
				textField.setText(text);
				textField.requestFocus();
				textField.selectAll();
				EventQueue.invokeLater(()->{
					revalidate();
					repaint();
				});
			}
		}
	}
	
	private class TextFieldFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent arg0) {
			changeToLabel();
		}
	}
	
	private class TextFieldKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent arg0) {
			if(arg0.getKeyCode()==KeyEvent.VK_ENTER) {
				String newText=textField.getText();
				if(newText.length()<1) {
					newText=text;
				}
				newText=handleText(newText, text);
				
				textField.setText(newText);
				if(!newText.equals(text)) {
					text=onEdit.apply(new String[] {newText, text});
				}
				changeToLabel();
			}else if(arg0.getKeyCode()==KeyEvent.VK_ESCAPE) {
				changeToLabel();
			}
		}
	}
	

	private void changeToLabel() {
		remove(textField);
		setText(text);
		add(label, "cell 0 0,alignx center,growy");
		EventQueue.invokeLater(()->{
			revalidate();
			repaint();
		});
	}
	
	
	
	public void setText(String text) {
		this.text=text;
		label.setText(text);
	}
	
	protected String handleText(String text, String alternativeText) {
		return text;
	}

}
