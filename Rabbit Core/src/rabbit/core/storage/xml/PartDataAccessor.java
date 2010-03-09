package rabbit.core.storage.xml;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import rabbit.core.internal.storage.xml.AbstractAccessor;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventType;

/**
 * Gets data about part usage.
 */
public class PartDataAccessor extends AbstractAccessor<PartEventType, PartEventListType> {

	/** Constructor. */
	public PartDataAccessor() {
		super();
	}

	@Override
	public Collection<PartEventListType> getCategories(EventListType doc) {
		return doc.getPartEvents();
	}

	@Override
	public String getId(PartEventType e) {
		return e.getPartId();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The keys of the map returned are command IDs, and the values are usage
	 * counts.
	 * </p>
	 */
	@Override
	public Map<String, Long> getData(Calendar start, Calendar end) {
		return super.getData(start, end);
	}

	@Override
	public long getUsage(PartEventType e) {
		return e.getDuration();
	}

	@Override
	public Collection<PartEventType> getXmlTypes(PartEventListType list) {
		return list.getPartEvent();
	}

	@Override
	public IDataStore getDataStore() {
		return DataStore.PART_STORE;
	}
}
