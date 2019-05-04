package functions.matedit;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import main.MatReceiver;
import parameters.BinaryIntParameter;
import parameters.components.ParameterNumberSliderPanel;
import view.MatPanel;
import view.MatReceiverPanel;

public class Switch extends MatEditFunction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7522333052993397688L;
	
	private static Switch instance;
	
	/**
	 * @return the instance
	 */
	public static Switch getInstance() {
		return instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public static void setInstance(Switch instance) {
		Switch.instance = instance;
	}

	public Switch() {
		super(
			new BinaryIntParameter("outputs", 0,0,63)
		);
		instance=this;
	}
	
	public void setOutputs(int output) {
		BinaryIntParameter param=(BinaryIntParameter) getParameter("outputs");
		param.setValue(output);
	}
	
	private boolean[] translateOutputs() {
		
		BinaryIntParameter param=(BinaryIntParameter) getParameter("outputs");

		String s=ParameterNumberSliderPanel.getBinaryNumberString(param.getValue(), 6);
		boolean b[]=new boolean[s.length()];
		
		int j=0;
		for(int i=s.length()-1;i>=0;i--) {
			b[j++]=s.charAt(i)=='1';
		}
		
		return b;
	}

	@Override
	protected Mat apply(Mat matIn) {
		return matIn.clone();
	}

	
	@Override
	protected void sendMat(Mat mat) {
		List<MatReceiver> list=new ArrayList<MatReceiver>(getReceivers());
		
		boolean b[]=translateOutputs();
		int i=0;
		for(MatReceiver receiver:list) {
			if(receiver!=null) {
				if(receiver instanceof MatReceiverPanel) {
					receiver.onReceive(mat, this);
					continue;
				}else if(i>=b.length || b[i]) {
					receiver.onReceive(mat, this);
				}
				i++;
			}
		}
		
	}
}
