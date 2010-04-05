package rabbit.data.xml.access;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

import org.joda.time.LocalDate;

import java.util.Collection;

public class PerspectiveDataAccessor2
    extends
    AbstractDataNodeAccessor<PerspectiveDataDescriptor, PerspectiveEventType, PerspectiveEventListType> {

  @Override
  protected PerspectiveDataDescriptor createDataNode(LocalDate cal,
      PerspectiveEventType type) {

    try {
      return new PerspectiveDataDescriptor(cal, type.getDuration(), type
          .getPerspectiveId());
      
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<PerspectiveEventType> getXmlTypes(
      PerspectiveEventListType list) {
    return list.getPerspectiveEvent();
  }

  @Override
  protected Collection<PerspectiveEventListType> getCategories(EventListType doc) {
    return doc.getPerspectiveEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.PERSPECTIVE_STORE;
  }

}
