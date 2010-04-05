package rabbit.data.xml.access;

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;

import org.joda.time.LocalDate;

import java.util.Collection;

// TODO test
public class PartDataAccessor2
    extends
    AbstractDataNodeAccessor<PartDataDescriptor, PartEventType, PartEventListType> {

  @Override
  protected Collection<PartEventListType> getCategories(EventListType doc) {
    return doc.getPartEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.PART_STORE;
  }

  @Override
  protected PartDataDescriptor createDataNode(LocalDate cal, PartEventType type) {
    try {
      return new PartDataDescriptor(cal, type.getDuration(), type.getPartId());
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<PartEventType> getXmlTypes(PartEventListType list) {
    return list.getPartEvent();
  }
}
