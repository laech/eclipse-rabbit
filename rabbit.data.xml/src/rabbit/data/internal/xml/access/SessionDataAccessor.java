package rabbit.data.internal.xml.access;

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.SessionData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Collection;

/**
 * Accesses session data events.
 */
public class SessionDataAccessor extends
    AbstractAccessor<ISessionData, SessionEventType, SessionEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @throws NullPointerException If argument is null.
   */
  @Inject
  SessionDataAccessor(@Named(StoreNames.SESSION_STORE) IDataStore store) {
    super(store);
  }

  @Override
  protected ISessionData createDataNode(LocalDate date,
      WorkspaceStorage workspace, SessionEventType type) throws Exception {
    final long startTime = type.getStartTime();
    final LocalTime time = startTime > 0 ? new LocalTime(startTime) : null;
    final Duration duration = new Duration(type.getDuration());
    return new SessionData(date, workspace, duration, time);
  }

  @Override
  protected Collection<SessionEventListType> getCategories(EventListType list) {
    return list.getSessionEvents();
  }

  @Override
  protected Collection<SessionEventType> getElements(SessionEventListType list) {
    return list.getSessionEvent();
  }
}
