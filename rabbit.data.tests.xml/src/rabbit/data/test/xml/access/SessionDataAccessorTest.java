package rabbit.data.test.xml.access;

import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;
import rabbit.data.test.xml.AbstractDataNodeAccessorTest;
import rabbit.data.xml.access.SessionDataAccessor;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see SessionDataAccessor
 */
@SuppressWarnings("restriction")
public class SessionDataAccessorTest extends 
    AbstractDataNodeAccessorTest<SessionDataDescriptor, SessionEventType, SessionEventListType> {

  @Override
  protected SessionDataAccessor create() {
    return new SessionDataAccessor();
  }

  @Override
  public void testCreateDataNode() throws Exception {
    LocalDate date = new LocalDate(1999, 1, 2);
    long duration = 982387934;
    SessionEventType type = new SessionEventType();
    type.setDuration(duration);
    SessionDataDescriptor des = createDataNode(accessor, date, type);
    assertEquals(date, des.getDate());
    assertEquals(duration, des.getValue());
  }

  @Override
  protected SessionEventListType createCategory() {
    SessionEventListType list = new SessionEventListType();
    list.setDate(DatatypeUtil.toXmlDate(new LocalDate()));
    return list;
  }

  @Override
  protected SessionEventType createElement() {
    SessionEventType type = new SessionEventType();
    type.setDuration(19834);
    return type;
  }

  @Override
  protected List<SessionEventType> getElements(SessionEventListType list) {
    return list.getSessionEvent();
  }

  @Override
  protected void setId(SessionEventType type, String id) {
    // Nothing to do, SessionEventType has no ID.
  }

  @Override
  protected void setValue(SessionEventType type, long usage) {
    type.setDuration(usage);
  }
}
