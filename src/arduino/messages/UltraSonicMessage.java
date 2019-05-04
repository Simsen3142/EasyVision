package arduino.messages;

public class UltraSonicMessage extends JSONMessage {

    public UltraSonicMessage(int type) {
        super(type);
    }

    
    public static class UltraSonicRequest extends UltraSonicMessage {
		public UltraSonicRequest() {
            super(30);
        }
    }
    
    public static class UltraSonicResponse extends UltraSonicMessage {
    	private double distance;
    	
		/**
		 * @return the distance
		 */
		public double getDistance() {
			return distance;
		}

		public UltraSonicResponse(double distance) {
            super(31);
            this.distance=distance;
        }
    }
}
