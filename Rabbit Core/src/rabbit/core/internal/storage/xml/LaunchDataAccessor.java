package rabbit.core.internal.storage.xml;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventType;
import rabbit.core.storage.LaunchDescriptor;

/**
 * Gets launch event data.
 */
public class LaunchDataAccessor extends
		AbstractAccessor<Set<LaunchDescriptor>, LaunchEventType, LaunchEventListType> {

	@Override
	protected Set<LaunchDescriptor> filter(List<LaunchEventListType> data) {
		Set<LaunchDescriptor> result = new HashSet<LaunchDescriptor>();
		
		for (LaunchEventListType list : data) {
			for (LaunchEventType type : list.getLaunchEvent()) {
				
				LaunchDescriptor launch = new LaunchDescriptor();
				launch.setDuration(type.getDuration());
				launch.setFileIds(type.getFileId());
				launch.setLaunchName(type.getLaunchName());
				launch.getLaunchTime().setTimeInMillis(
						type.getLaunchTime().toGregorianCalendar().getTimeInMillis());
				launch.setLaunchTypeId(type.getLaunchTypeId());
				launch.setLaunchModeId(type.getLaunchModeId());

				result.add(launch);
			}
		}
		return result;
	}

	@Override
	protected Collection<LaunchEventListType> getCategories(EventListType doc) {
		return doc.getLaunchEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.LAUNCH_STORE;
	}

}
