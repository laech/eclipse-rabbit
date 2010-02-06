package rabbit.core.internal;

import rabbit.core.ITracker;

/**
 * An object of this type will created when reading the extension point.
 */
public class TrackerObject {

	private String id;
	private String name;
	private String description;
	private ITracker<?> tracker;

	/**
	 * Constructs a new tracker object.
	 * 
	 * @param id
	 *            The id of the tracker.
	 * @param name
	 *            The name of the tracker.
	 * @param description
	 *            The description of the tracker.
	 * @param t
	 *            The tracker.
	 */
	public TrackerObject(String id, String name, String description, ITracker<?> t) {
		this.id = id;
		this.name = name;
		this.description = description;
		tracker = t;
	}

	/**
	 * Gets the id of the tracker.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the tracker.
	 * 
	 * @param id
	 *            The new id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the name of the tracker.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the tracker.
	 * 
	 * @param name
	 *            The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description of the tracker.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the tracker.
	 * 
	 * @param description
	 *            The new description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the tracker.
	 * 
	 * @return The tracker.
	 */
	public ITracker<?> getTracker() {
		return tracker;
	}

	/**
	 * Sets the tracker.
	 * 
	 * @param tracker
	 *            The new tracker.
	 */
	public void setTracker(ITracker<?> tracker) {
		this.tracker = tracker;
	}
}
