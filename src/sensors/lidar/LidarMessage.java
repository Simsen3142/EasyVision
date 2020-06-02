package sensors.lidar;

import java.io.Serializable;

public class LidarMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2845189197863165813L;
	private final double distance;
	private final double degree;
	private final double quality;
	private final long time;
	private double rad;
	private double x;
	private double y;
	
	public long getTime() {
		return time;
	}

	public double getDistance() {
		return distance;
	}

	public double getDegree() {
		return degree;
	}

	public double getQuality() {
		return quality;
	}
	
	public double getRad() {
		if(rad==0)
			rad=(Math.PI / 180.0)*degree;
		return rad;
	}
	
	public double getX() {
		if(x==0) {
			x=Math.sin(getRad())*distance;
		}
		return x;
	}

	public double getY() {
		if(y==0) {
			y=-Math.cos(getRad())*distance;
		}
		return y;
	}

	public LidarMessage(double distance, double degree, double quality) {
		this.distance = distance;
		this.degree = degree;
		this.quality = quality;
		time=System.currentTimeMillis();
	}
	
	public double distanceTo(LidarMessage msg) {
		if(msg==null) {
			return -1;
		}
		double d=Math.sqrt(Math.pow(getX()-msg.getX(), 2)+Math.pow(getY()-msg.getY(), 2));
		return d;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(degree);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(distance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(quality);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LidarMessage other = (LidarMessage) obj;
		if (Double.doubleToLongBits(degree) != Double.doubleToLongBits(other.degree))
			return false;
		if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance))
			return false;
		if (Double.doubleToLongBits(quality) != Double.doubleToLongBits(other.quality))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LidarMessage [distance=" + String.format("%,.2f", distance) + ", degree="
				+ String.format("%,.2f", degree) + ", quality=" + String.format("%,.2f", quality) 
				+", x"+ String.format("%,.2f", getX())+", y"+ String.format("%,.2f", getY())+ "]";
	}
}
