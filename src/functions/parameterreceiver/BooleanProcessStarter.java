package functions.parameterreceiver;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import functions.RepresentationIcon;
import functions.Startable;
import functions.Stoppable;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class BooleanProcessStarter extends ParameterizedObject implements ParameterReceiver, RepresentationIcon {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7137287689954563675L;
	private int id = System.identityHashCode(this);

	private List<Startable> startables;

	public List<Startable> getStartables() {
		return startables;
	}

	public void clearStartables() {
		startables.clear();
	}

	public boolean addStartable(Startable startable) {
		if (startable != null)
			return startables.add(startable);
		return false;
	}

	public boolean removeStartable(Object startable) {
		return startables.remove(startable);
	}

	@Override
	public void recalculateId() {
		this.id *= Math.random();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public BooleanProcessStarter(Boolean empty) {

	}

	public BooleanProcessStarter() {
		super();
		startables = new ArrayList<Startable>();
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters, ParameterizedObject sender) {
		boolean b = getFirstFittingParameter(parameters, BooleanParameter.class).getValue();
		if (b) {
			startables.forEach((stbl) -> {
				if (!stbl.isStarted()) {
					stbl.start();
				}
			});
		} else {
			startables.forEach((stbl) -> {
				if (stbl.isStarted()) {
					stbl.stop();
				}
			});
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BooleanProcessStarter))
			return false;
		BooleanProcessStarter other = (BooleanProcessStarter) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Image getRepresentationImage() {
		return null;
	}

	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(() -> {
			Image img = getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}
}
