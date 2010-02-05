package rabbit.tracking.storage.xml;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import rabbit.tracking.storage.xml.internal.DataStore;
import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

/**
 * Gets data about part usage.
 */
public class PartDataAccessor extends AbstractXmlAccessor<PartEventType, PartEventListType> {

	/** Constructor. */
	public PartDataAccessor() {
		super();
	}

	@Override protected Collection<PartEventListType> getCategories(EventListType doc) {
		return doc.getPartEvents();
	}

	@Override protected String getId(PartEventType e) {
		return e.getPartId();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The keys of the map returned are command IDs, and the values are usage
	 * counts.
	 * </p>
	 */
	@Override public Map<String, Long> getData(Calendar start, Calendar end) {
		return super.getData(start, end);
	}

	@Override protected long getUsage(PartEventType e) {
		return e.getDuration();
	}

	@Override protected Collection<PartEventType> getXmlTypes(PartEventListType list) {
		return list.getPartEvent();
	}

	@Override protected IDataStore getDataStore() {
		return DataStore.PART_STORE;
	}
}
