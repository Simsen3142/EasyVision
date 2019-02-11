package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serializing {
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
	
	private static void createFileWithDirectories(File f) {
		File parent = f.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
	}
}
