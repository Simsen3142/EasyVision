package arduino.messages;

public class MotorMessage extends JSONMessage {

    public MotorMessage(int type) {
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
            super(10);
            this.motor1=motor1;
            this.motor2=motor2;
        }
    }
    
    public static class MotorStopMessage extends MotorMessage {
		public MotorStopMessage() {
            super(11);
        }
    }
    
    public static class MotorStartMessage extends MotorMessage {
		public MotorStartMessage() {
            super(12);
        }
    }
    
    public static class MotorStepMessage extends MotorMessage {
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

		public MotorStepMessage(int motor1, int motor2) {
            super(13);
            this.motor1=motor1;
            this.motor2=motor2;
        }
    }
    
    public static class MotorStepsDoneMessage extends MotorMessage {
    	private boolean done;
    	
    	/**
		 * @return the done
		 */
		public boolean isDone() {
			return done;
		}

		public MotorStepsDoneMessage(boolean done) {
            super(14);
            this.done=done;
        }
    }
}
