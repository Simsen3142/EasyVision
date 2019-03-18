import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

public class Test {
	public enum Xyz {
		X,Y,Z;
	}
	
	public Enum<?> getXyz(){
		return Xyz.X;
	}
	
	public void test() {
		JComboBox<Enum<?>> combobox=new JComboBox<>(getXyz().getDeclaringClass().getEnumConstants());
	}
	
	public static void main(String[] da) {
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
		
		JList list=new JList();
	}
}
