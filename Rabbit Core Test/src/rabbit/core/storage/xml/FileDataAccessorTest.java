package rabbit.core.storage.xml;

import rabbit.core.internal.storage.xml.AbstractAccessorTest;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;

/**
 * Test for {@link FileDataAccessor}
 */
public class FileDataAccessorTest extends AbstractAccessorTest<FileEventType, FileEventListType> {

	@Override
	protected FileDataAccessor create() {
		return new FileDataAccessor();
	}

	@Override
	protected FileEventListType createListType() {
		return objectFactory.createFileEventListType();
	}

	@Override
	protected FileEventType createXmlType() {
		return objectFactory.createFileEventType();
	}

	@Override
	protected void setId(FileEventType type, String id) {
		type.setFileId(id);
	}

	@Override
	protected void setUsage(FileEventType type, long usage) {
		type.setDuration(usage);
	}

}
