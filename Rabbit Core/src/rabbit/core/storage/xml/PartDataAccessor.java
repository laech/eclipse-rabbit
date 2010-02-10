package rabbit.core.storage.xml;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import rabbit.core.internal.storage.xml.AbstractAccessor;
import rabbit.core.internal.storage.xml.DataStore;
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
	protected Collection<PartEventListType> getCategories(EventListType doc) {
		return doc.getPartEvents();
	}

	@Override
	protected String getId(PartEventType e) {
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
	protected long getUsage(PartEventType e) {
		return e.getDuration();
	}

	@Override
	protected Collection<PartEventType> getXmlTypes(PartEventListType list) {
		return list.getPartEvent();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PART_STORE;
	}
}
