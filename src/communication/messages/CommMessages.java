package communication.messages;

public class CommMessages extends JSONMessage {

    public CommMessages(int type) {
        super(type);
    }

    
    public static class ResendMessage extends CommMessages {
		public ResendMessage() {
            super(50);
        }
    }
}
