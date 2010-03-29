package rabbit.core.storage;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes a launch.
 */
public class LaunchDescriptor {

	public static enum Mode {
		DEBUG_MODE, PROFILE_MODE, RUN_MODE, UNKNOWN;
	}

	private long duration;
	private Mode launchMode;
	private String launchName;
	private Calendar launchTime;
	private Set<String> fileIds;
	private String launchType;

	/**
	 * Constructs a new descriptor.
	 */
	public LaunchDescriptor() {
		launchTime = new GregorianCalendar(0, 0, 0);
		fileIds = Collections.emptySet();
		launchMode = Mode.UNKNOWN;
		launchName = "";
		launchType = "";
		duration = 0;
	}

	/**
	 * Ges the duration of the launch.
	 * 
	 * @return The duration.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Gets the IDs of the files involved.
	 * 
	 * @return The IDs of the files involved.
	 * @see {@link rabbit.core.storage.IFileMapper}
	 */
	public Set<String> getFileIds() {
		return fileIds;
	}

	/**
	 * Gets the launch mode.
	 * 
	 * @return The launch mode.
	 */
	public Mode getLaunchMode() {
		return launchMode;
	}

	/**
	 * Gets the name of this launch.
	 * 
	 * @return The name of this launch.
	 */
	public String getLaunchName() {
		return launchName;
	}

	/**
	 * Gets the time of this launch.
	 * 
	 * @return The time of this launch, the calendar object can be manipulated
	 *         directly to make changes.
	 */
	public Calendar getLaunchTime() {
		return launchTime;
	}

	/**
	 * Sets the duration of this launch.
	 * 
	 * @param duration
	 *            The duration.
	 * @throws IllegalArgumentException
	 *             If duration < 0.
	 */
	public void setDuration(long duration) {
		if (duration < 0) {
			throw new IllegalArgumentException();
		}
		this.duration = duration;
	}

	/**
	 * Sets the IDs of the files involved in this launch.
	 * 
	 * @param fileIds
	 *            The IDs of the files.
	 * @see {@link rabbit.core.storage.IFileMapper}
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setFileIds(Collection<String> fileIds) {
		if (fileIds == null) {
			throw new NullPointerException();
		}
		this.fileIds = Collections.unmodifiableSet(new HashSet<String>(fileIds));
	}

	/**
	 * Sets the launch mode of this launch.
	 * 
	 * @param launchMode
	 *            The launch mode.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setLaunchMode(Mode launchMode) {
		if (launchMode == null) {
			throw new NullPointerException();
		}
		this.launchMode = launchMode;
	}

	/**
	 * Sets the name of this launch.
	 * 
	 * @param launchName
	 *            The name of this launch.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setLaunchName(String launchName) {
		if (launchName == null) {
			throw new NullPointerException();
		}
		this.launchName = launchName;
	}

	/**
	 * Gets the type of this launch.
	 * 
	 * @return The type of this launch.
	 */
	public String getLaunchType() {
		return launchType;
	}

	/**
	 * Sets the type of this launch.
	 * 
	 * @param launchType
	 *            The type of this launch.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setLaunchType(String launchType) {
		if (launchType == null) {
			throw new NullPointerException();
		}
		this.launchType = launchType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		LaunchDescriptor des = (LaunchDescriptor) obj;
		return getDuration() == des.getDuration()
				&& getLaunchMode() == des.getLaunchMode()
				&& getLaunchName().equals(des.getLaunchName())
				&& getLaunchTime().getTime().equals(des.getLaunchTime().getTime())
				&& getLaunchType().equals(des.getLaunchType())
				// Test to see all fileIds are same:
				&& getFileIds().containsAll(des.getFileIds())
				&& des.getFileIds().containsAll(getFileIds());
	}

	@Override
	public int hashCode() {
		return (getLaunchMode().hashCode()
				+ getLaunchName().hashCode()
				+ getLaunchTime().hashCode()
				+ getLaunchType().hashCode() + getFileIds().hashCode()) % 31;
	}
}
