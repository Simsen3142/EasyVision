package parameters.components;

import components.EditableLabel;
import external.FileDrop;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import javax.swing.JButton;

public class ParameterFileChoosePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4985687151113362113L;
	private FileParameter parameter;
	private ParameterFileChoosePanel pnl=this;
	private EditableLabel lblTitle;
	private Function<Void,Void> onSetValue;
	private Function<ParameterFileChoosePanel,Void> onNoFileChosen;
	private JButton btnChoosefile;
	private static JFileChooser fileChooser;
	private ParameterFileChoosePanel instance=this;
	private JLabel lblValue;
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(lblTitle!=null)
			lblTitle.setForeground(c);
	}
	
	public void declareOnSetValue(Function<Void,Void> onSetValue) {
		this.onSetValue=onSetValue;
	}
	
	public void declareOnNoFileChosen(Function<ParameterFileChoosePanel,Void> onNoFileChosen) {
		this.onNoFileChosen=onNoFileChosen;
	}
	
	public ParameterFileChoosePanel(FileParameter parameter) {
		this.parameter=parameter;
		initialize();
	}
	
	private void initialize() {
		setOpaque(false);
		setLayout(new MigLayout("gap 0, insets 0", "[30%][60%][10%]", "[grow]"));
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 0 0,growx,aligny center");
		
		btnChoosefile = new JButton("File");
		btnChoosefile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser==null) {
					fileChooser=new JFileChooser();
				}
				int i=fileChooser.showSaveDialog(null);
				if(i==JFileChooser.APPROVE_OPTION) {
					File file=fileChooser.getSelectedFile();
					if(!file.exists()) {
						try {
							file.createNewFile();
							setValue(file);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if(onSetValue!=null)
						onSetValue.apply(null);
				}else if(getValue()==null) {
					onNoFileChosen.apply(instance);
				}
			}
		});
		add(btnChoosefile, "cell 2 0,alignx center");
		
		lblValue = new JLabel();
		if(parameter.getValue()!=null)
			lblValue.setText(parameter.getValue().getAbsolutePath());
		add(lblValue, "cell 1 0,alignx center");
		
		new FileDrop(this, new FileDrop.Listener() {
			@Override
			public void filesDropped(File[] files) {
				setValue(files[0]);
			}
		});
	}
	
	public void setValue(File file) {
		parameter.setValue(file);
		lblValue.setText(file.getAbsolutePath());
	}
	
	public File getValue() {
		return parameter.getValue();
	}
}
