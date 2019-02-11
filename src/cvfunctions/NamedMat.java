package cvfunctions;

import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Rect;

public class NamedMat extends Mat{
	private String name;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public NamedMat(String name) {
		super();
		this.setName(name);
	}
	
	public NamedMat(String name, long addr) {
		super(addr);
		this.setName(name);
	}
	
	public NamedMat(String name, Mat m, Range rowRange) {
		super(m, rowRange);
		this.setName(name);
	}
	
	public NamedMat(String name, Mat m, Rect roi) {
		super(m, roi);
		this.setName(name);
	}
	
	public NamedMat(String name, Mat m) {
		this(name, m, new Rect(0, 0, m.cols(), m.rows()));
	}
}
