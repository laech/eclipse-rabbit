package rabbit.data.internal.xml.access;

import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses session data events.
 */
public class SessionDataAccessor extends
    AbstractDataNodeAccessor<SessionDataDescriptor, 
                             SessionEventType, 
                             SessionEventListType> {


  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @param merger The merger for merging XML data nodes.
   * @throws NullPointerException If any arguments are null.
   */
  @Inject
  SessionDataAccessor(
      @Named(StoreNames.SESSION_STORE) IDataStore store,
      IMerger<SessionEventType> merger) {
    super(store, merger);
  }

  @Override
  protected SessionDataDescriptor createDataNode(LocalDate cal,
      SessionEventType type) {
    if (cal != null && type.getDuration() >= 0) {
      return new SessionDataDescriptor(cal, new Duration(type.getDuration()));
    } else {
      return null;
    }
  }

  @Override
  protected Collection<SessionEventType> getElements(
      SessionEventListType category) {
    return category.getSessionEvent();
  }

  @Override
  protected Collection<SessionEventListType> getCategories(EventListType doc) {
    return doc.getSessionEvents();
  }
}
