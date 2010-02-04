package rabbit.tracking.storage.xml;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.event.FileEvent;
import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.FileEventListType;
import rabbit.tracking.storage.xml.schema.FileEventType;

public class FileEventStorer extends
		AbstractXmlStorer<FileEvent, FileEventType, FileEventListType> {

	@Override
	protected List<FileEventListType> getXmlTypeCategories(EventListType events) {
		return events.getFileEvents();
	}

	@Override
	protected boolean hasSameId(FileEventType x, FileEvent e) {
		return x.getFilePath().equals(e.getFile().getFullPath().toString());
	}

	@Override
	protected boolean hasSameId(FileEventType x1, FileEventType x2) {
		return x1.getFilePath().equals(x2.getFilePath());
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
		type.setFilePath(e.getFile().getFullPath().toString());
		return type;
	}

	@Override
	protected FileEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		FileEventListType type = OBJECT_FACTORY.createFileEventListType();
		type.setDate(date);
		return type;
	}

	@Override
	public IDataStore getDataStore() {
		return DataStore.FILE_STORE;
	}

}
