package rabbit.data.xml.access;

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
    return new SessionDataDescriptor(cal, type.getDuration());
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

//  /** Uses a perspective data accessor to get the data out. */
//  private final IAccessor<PerspectiveDataDescriptor> accessor;
//
//  /**
//   * Constructor.
//   */
//  public SessionDataAccessor() {
//    accessor = new PerspectiveDataAccessor();
//  }
//
//  @Override
//  public ImmutableCollection<SessionDataDescriptor> getData(LocalDate start,
//                                                            LocalDate end) {
//
//    Collection<PerspectiveDataDescriptor> data = accessor.getData(start, end);
//
//    Map<LocalDate, Long> map = Maps.newLinkedHashMap();
//    for (PerspectiveDataDescriptor des : data) {
//      Long value = map.get(des.getDate());
//      map.put(des.getDate(),
//          (value != null) ? value + des.getValue() : des.getValue());
//    }
//    
//    ImmutableSet.Builder<SessionDataDescriptor> result = ImmutableSet.builder();
//    for (Map.Entry<LocalDate, Long> entry : map.entrySet())
//      result.add(new SessionDataDescriptor(entry.getKey(), entry.getValue()));
//
//    return result.build();
//  }

}
