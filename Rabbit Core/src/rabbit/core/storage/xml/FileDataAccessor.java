package rabbit.core.storage.xml;

import java.util.Collection;

import rabbit.core.internal.storage.xml.AbstractAccessor;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;

/**
 * 
 */
public class FileDataAccessor extends AbstractAccessor<FileEventType, FileEventListType> {

	@Override
	public Collection<FileEventListType> getCategories(EventListType doc) {
		return doc.getFileEvents();
	}

	@Override
	public IDataStore getDataStore() {
		return DataStore.FILE_STORE;
	}

	@Override
	public String getId(FileEventType e) {
		return e.getFileId();
	}

	@Override
	public long getUsage(FileEventType e) {
		return e.getDuration();
	}

	@Override
	public Collection<FileEventType> getXmlTypes(FileEventListType list) {
		return list.getFileEvent();
	}

}
