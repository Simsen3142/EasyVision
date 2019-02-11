package database;

import java.io.File;

public enum SerializedObjectsLocation {
	BASE("data/serialized", true), CAMERA(BASE.path+"/camera"), MATEDITFUNCTION(BASE.path+"/mateditfunction"), SESSIONS(BASE.path+"/sessions");
	
	private String path;
	
	
	SerializedObjectsLocation(String path) {
		this.path=path;
	}
	
	SerializedObjectsLocation(String path, boolean first) {
		this.path=path;
	}
	
	public File getFile(String filename) {
		return new File(this.path+"/"+filename);
	}
	
	
}

