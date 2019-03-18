package database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import javax.swing.JFileChooser;

public class Serializing {
	private static JFileChooser fileChooser;
	
	/**
	 * @return the fileChooser
	 */
	public static JFileChooser getFileChooser() {
		if(fileChooser==null)
			fileChooser=new JFileChooser();
		return fileChooser;
	}
	
	public static File showSaveDialog() {
		File f=null;
		
		int i=getFileChooser().showSaveDialog(null);
		if(i==JFileChooser.APPROVE_OPTION) {
			f=getFileChooser().getSelectedFile();
		}
		
		return f;
	}
	
	public static File showOpenDialog() {
		File f=null;
		
		int i=getFileChooser().showOpenDialog(null);
		if(i==JFileChooser.APPROVE_OPTION) {
			f=getFileChooser().getSelectedFile();
		}
		
		return f;
	}

	public static boolean serialize(Serializable s, File f) {
		boolean success=false;
	    // save the object to file
	    FileOutputStream fos = null;
	    ObjectOutputStream out = null;
	    try {
	    	if(!f.exists())
	    		createFileWithDirectories(f);
	        fos = new FileOutputStream(f);
	        out = new ObjectOutputStream(fos);
	        out.writeObject(s);

	        out.close();
	        fos.close();
	        success=true;
	    } catch (Exception ex) {
	    	System.out.println(f);
	        ex.printStackTrace();
	    }
	    return success;
	}
	
	
	public static Serializable deSerialize(File f) {
		Serializable s=null;
		FileInputStream fis = null;
	    ObjectInputStream in = null;
	    try {
	        fis = new FileInputStream(f);
	        in = new ObjectInputStream(fis);
	        s = (Serializable) in.readObject();
	        in.close();
	        fis.close();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
	    return s;
	}
	
	public static Serializable deSerialize(String s) {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois;
		Serializable ser=null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(data));
			ser = (Serializable) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
		return ser;
	}

	public static String serialize(Serializable o) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(baos.toByteArray()); 
	}
	
	private static void createFileWithDirectories(File f) {
		File parent = f.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
	}
}
