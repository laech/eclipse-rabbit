package rabbit.core.internal.storage.xml;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;

import rabbit.core.events.FileEvent;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;
import rabbit.core.storage.xml.FileEventStorer;

public class FileEventStorerTest
		extends AbstractXmlStorerTest2<FileEvent, FileEventType, FileEventListType> {

	@Override
	protected List<FileEventType> getEventTypes(FileEventListType type) {
		return type.getFileEvent();
	}

	@Override
	protected boolean isEqual(FileEventType type, FileEvent event) {
		boolean isEqual = type.getFileId().equals(event.getFileId());
		if (isEqual) {
			isEqual = (type.getDuration() == event.getDuration());
		}
		return isEqual;
	}

	@Override
	protected void mergeValue(FileEvent main, FileEvent tmp) {
		main.setDuration(tmp.getDuration() + main.getDuration());
	}

	@Override
	protected FileEventStorer create() {
		return new FileEventStorer();
	}

	@Override
	protected FileEvent createEvent() {
		return new FileEvent(Calendar.getInstance(), 10, "someId");
	}

	@Override
	protected FileEvent createEvent2() {
		return new FileEvent(Calendar.getInstance(), 110, "blah");
	}

	@Override
	public void testHasSameId_typeAndEvent() {
		String id = "asdfsdf23";
		FileEventType type = new ObjectFactory().createFileEventType();
		type.setFileId(id);
		FileEvent event = new FileEvent(Calendar.getInstance(), 10, id);
		Assert.assertTrue(storer.hasSameId(type, event));
	}

	@Override
	public void testHasSameId_typeAndType() {
		String id = "asdfsdf23";
		FileEventType type1 = new ObjectFactory().createFileEventType();
		type1.setFileId(id);
		FileEventType type2 = new ObjectFactory().createFileEventType();
		type2.setFileId(id);
		Assert.assertTrue(storer.hasSameId(type1, type2));
	}

	@Override
	protected long getValue(FileEventType type) {
		return type.getDuration();
	}

	@Override
	protected long getValue(FileEvent event) {
		return event.getDuration();
	}

}
