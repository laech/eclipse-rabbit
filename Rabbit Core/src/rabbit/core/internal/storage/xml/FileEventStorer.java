package rabbit.core.internal.storage.xml;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.events.FileEvent;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;

public class FileEventStorer extends
		AbstractStorer<FileEvent, FileEventType, FileEventListType> {

	@Override
	protected List<FileEventListType> getXmlTypeCategories(EventListType events) {
		return events.getFileEvents();
	}

	@Override
	protected boolean hasSameId(FileEventType x, FileEvent e) {
		return x.getFileId().equals(e.getFileId());
	}

	@Override
	protected boolean hasSameId(FileEventType x1, FileEventType x2) {
		return x1.getFileId().equals(x2.getFileId());
	}

	@Override
	protected void merge(FileEventType main, FileEvent e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	protected void merge(FileEventType main, FileEventType x) {
		main.setDuration(main.getDuration() + x.getDuration());
	}

	@Override
	protected void merge(FileEventListType main, FileEvent e) {
		merge(main.getFileEvent(), e);
	}

	@Override
	protected void merge(FileEventListType main, FileEventListType data) {
		merge(main.getFileEvent(), data.getFileEvent());
	}

	@Override
	protected FileEventType newXmlType(FileEvent e) {
		FileEventType type = OBJECT_FACTORY.createFileEventType();
		type.setDuration(e.getDuration());
		type.setFileId(e.getFileId());
		return type;
	}

	@Override
	protected FileEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		FileEventListType type = OBJECT_FACTORY.createFileEventListType();
		type.setDate(date);
		return type;
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.FILE_STORE;
	}

}
