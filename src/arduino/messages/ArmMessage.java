package arduino.messages;

public class ArmMessage extends JSONMessage {

    public ArmMessage(int type) {
        super(type);
    }

    
    public static class ArmEnableMessage extends ArmMessage {
		public ArmEnableMessage() {
            super(40);
        }
    }
    
    public static class ArmDisableMessage extends ArmMessage {
		public ArmDisableMessage() {
            super(41);
        }
    }
    
    public static class ArmPosition1Message extends ArmMessage {
		public ArmPosition1Message() {
            super(42);
        }
    }
    
    public static class ArmPosition2Message extends ArmMessage {
		public ArmPosition2Message() {
            super(43);
        }
    }
    
    public static class PickUpMessage extends ArmMessage {
		public PickUpMessage() {
            super(44);
        }
    }
    
    public static class ReleaseMessage extends ArmMessage {
		public ReleaseMessage() {
            super(45);
        }
    }
    
    public static class EndPosReachedMessage extends ArmMessage {
		public EndPosReachedMessage() {
            super(46);
        }
    }
}
