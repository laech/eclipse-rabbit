package rabbit.data.xml.access;

import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;

import org.joda.time.LocalDate;

import java.util.Collection;

public class FileDataAccessor2
    extends
    AbstractDataNodeAccessor<FileDataDescriptor, FileEventType, FileEventListType> {

  @Override
  protected FileDataDescriptor createDataNode(LocalDate cal, FileEventType type) {
    try {
      return new FileDataDescriptor(cal, type.getDuration(), type.getFileId());
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<FileEventType> getXmlTypes(FileEventListType list) {
    return list.getFileEvent();
  }

  @Override
  protected Collection<FileEventListType> getCategories(EventListType doc) {
    return doc.getFileEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.FILE_STORE;
  }

}
