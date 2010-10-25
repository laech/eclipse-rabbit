package rabbit.data.internal.xml.access2;

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.SessionData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.access.AbstractNodeAccessor;
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
    AbstractNodeAccessor<ISessionData, SessionEventType, SessionEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @param merger The merger for merging XML data nodes.
   * @throws NullPointerException If any arguments are null.
   */
  @Inject
  SessionDataAccessor(@Named(StoreNames.SESSION_STORE) IDataStore store,
      IMerger<SessionEventType> merger) {
    super(store, merger);
  }
  
  @Override
  protected ISessionData createDataNode(
      LocalDate cal, WorkspaceStorage ws, SessionEventType type) throws Exception {
    return new SessionData(cal, ws, new Duration(type.getDuration()));
  }

  @Override
  protected Collection<SessionEventType> getElements(SessionEventListType list) {
    return list.getSessionEvent();
  }

  @Override
  protected Collection<SessionEventListType> getCategories(EventListType list) {
    return list.getSessionEvents();
  }
}
