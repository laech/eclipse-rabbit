package rabbit.data.internal.xml.access;

import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.SessionEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses session data events.
 */
public class SessionDataAccessor extends 
    AbstractDataNodeAccessor<SessionDataDescriptor, SessionEventType, SessionEventListType> {

  @Override
  protected SessionDataDescriptor createDataNode(LocalDate cal, SessionEventType type) {
    if (cal != null && type.getDuration() >= 0) {
      return new SessionDataDescriptor(cal, type.getDuration());
    } else {
      return null;
    }
  }

  @Override
  protected IMerger<SessionEventType> createMerger() {
    return new SessionEventTypeMerger();
  }

  @Override
  protected Collection<SessionEventType> getElements(SessionEventListType category) {
    return category.getSessionEvent();
  }

  @Override
  protected Collection<SessionEventListType> getCategories(EventListType doc) {
    return doc.getSessionEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.SESSION_STORE;
  }

}
