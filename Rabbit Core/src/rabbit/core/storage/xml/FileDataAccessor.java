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
	protected Collection<FileEventListType> getCategories(EventListType doc) {
		return doc.getFileEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.FILE_STORE;
	}

	@Override
	protected String getId(FileEventType e) {
		return e.getFileId();
	}

	@Override
	protected long getUsage(FileEventType e) {
		return e.getDuration();
	}

	@Override
	protected Collection<FileEventType> getXmlTypes(FileEventListType list) {
		return list.getFileEvent();
	}

}
