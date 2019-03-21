package arduino.messages;

public class MotorMessage extends JSONMessage {

    public MotorMessage(String type) {
        super(type);
    }

    
    public static class MotorFrequencyMessage extends MotorMessage {
    	private int motor1;
		private int motor2;
		
        /**
		 * @return the motor2
		 */
		public int getMotor2() {
			return motor2;
		}

		/**
		 * @return the motor1
		 */
		public int getMotor1() {
			return motor1;
		}

		public MotorFrequencyMessage(int motor1, int motor2) {
            super("motor_frequency");
            this.motor1=motor1;
            this.motor2=motor2;
        }
    }
    
    public static class MotorStopMessage extends MotorMessage {
		public MotorStopMessage() {
            super("motor_stop");
        }
    }
}
