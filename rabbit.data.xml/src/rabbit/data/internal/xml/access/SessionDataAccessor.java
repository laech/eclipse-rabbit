package rabbit.data.internal.xml.access;

import static rabbit.data.internal.xml.StoreNames.SESSION_STORE;

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.SessionData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.IntervalType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import static com.google.common.collect.Lists.transform;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

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
  SessionDataAccessor(@Named(SESSION_STORE) IDataStore store) {
    super(store);
  }

  @Override
  protected ISessionData createDataNode(LocalDate date,
      WorkspaceStorage workspace, SessionEventType type) throws Exception {

    List<Interval> intervals = emptyList();
    if (!type.getInterval().isEmpty()) {
      intervals = transform(type.getInterval(),
          new Function<IntervalType, Interval>() {
            @Override
            public Interval apply(IntervalType input) {
              return new Interval(input.getStartTime(), input.getStartTime()
                  + input.getDuration());
            }
          });
    }
    final Duration duration = new Duration(type.getDuration());
    return new SessionData(date, workspace, duration, intervals);
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
