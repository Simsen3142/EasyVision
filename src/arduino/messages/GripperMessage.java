package arduino.messages;

public class GripperMessage extends JSONMessage {

    public GripperMessage(String type) {
        super(type);
    }

    
    public static class MotorFrequencyMessage extends GripperMessage {
		public MotorFrequencyMessage(int motor1, int motor2) {
            super("gripper_open");
        }
    }
    
    public static class MotorStopMessage extends GripperMessage {
		public MotorStopMessage() {
            super("gripper_close");
        }
    }
}
