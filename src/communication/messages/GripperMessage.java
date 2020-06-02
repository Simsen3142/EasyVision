package communication.messages;

public class GripperMessage extends JSONMessage {

    public GripperMessage(int type) {
        super(type);
    }

    
    public static class GripperOpenMessage extends GripperMessage {
		public GripperOpenMessage() {
            super(20);
        }
    }
    
    public static class GripperCloseMessage extends GripperMessage {
		public GripperCloseMessage() {
            super(21);
        }
    }
}
