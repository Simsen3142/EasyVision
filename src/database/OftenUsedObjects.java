package database;

import java.io.File;

public enum OftenUsedObjects{
		LIST_MATEDITFUNCTIONS("matEditFunctions",SerializedObjectsLocation.MATEDITFUNCTION), 
		LIST_CAMERA_RESOURCES("cameraResources",SerializedObjectsLocation.CAMERA), 
		SESSION("session",SerializedObjectsLocation.SESSIONS);
		
		private File file;
		
		OftenUsedObjects(String name, SerializedObjectsLocation loc){
			this.file=loc.getFile(name);
		}
		
		public File getFile() {
			return file;
		}

	}