package rabbit.data.test.xml.access;

import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.xml.access.SessionDataAccessor;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @see SessionDataAccessor
 */
@SuppressWarnings("restriction")
public class SessionDataAccessorTest {

  private SessionDataAccessor accessor = new SessionDataAccessor();

  private static final IPath originalRoot = XmlPlugin.getDefault()
      .getStoragePathRoot();

  @AfterClass
  public static void afterClass() {
    XmlPlugin.getDefault().setStoragePathRoot(originalRoot.toFile());
  }

  @BeforeClass
  public static void beforeClass() {
    // Set the directory root to a new folder so that we don't mess
    // with existing data:
    File dir = originalRoot.append(System.currentTimeMillis() + "").toFile();
    if (!dir.exists() && !dir.mkdirs()) {
      Assert.fail();
    }
    XmlPlugin.getDefault().setStoragePathRoot(dir);
  }

  @Test(expected = NullPointerException.class)
  public void testGetData_endDateNull() {
    accessor.getData(new LocalDate(), null);
  }

  @Test
  public void testGetData_listsWithDifferentDates() throws Exception {
    String id = "qfnnvkfde877thfg";

    // 1:

    int count1 = 298;
    PerspectiveEventType type1 = new PerspectiveEventType();
    type1.setPerspectiveId(id);
    type1.setDuration(count1);

    PerspectiveEventListType list1 = new PerspectiveEventListType();
    MutableDateTime tmp = new MutableDateTime();
    tmp.setDayOfMonth(1);
    XMLGregorianCalendar start = toXmlDate(tmp.toDateTime());
    list1.setDate(start);
    list1.getPerspectiveEvent().add(type1);

    // 2:

    int count2 = 22817;
    PerspectiveEventType type2 = new PerspectiveEventType();
    type2.setPerspectiveId(id);
    type2.setDuration(count2);

    PerspectiveEventListType list2 = new PerspectiveEventListType();
    tmp.setDayOfMonth(3);
    XMLGregorianCalendar end = toXmlDate(tmp.toDateTime());
    list2.setDate(end);
    list2.getPerspectiveEvent().add(type2);

    EventListType events = new EventListType();
    events.getPerspectiveEvents().add(list1);
    events.getPerspectiveEvents().add(list2);

    File f = DataStore.PERSPECTIVE_STORE.getDataFile(tmp.toDateTime()
        .toLocalDate());
    DataStore.PERSPECTIVE_STORE.write(events, f);

    assertValues(accessor.getData(new LocalDate(start.toGregorianCalendar()
        .getTimeInMillis()), new LocalDate(end.toGregorianCalendar()
        .getTimeInMillis())), events);
  }

  /**
   * Tests that two lists with the same date are stored, then getting the data
   * out should return the combined data. Note that although two lists with the
   * same date should not have happened.
   */
  @Test
  public void testGetData_listsWithSameDate() throws Exception {
    DateTime date = new DateTime();
    String id = "qfnnvkfde877thfg";

    // 1:

    int count1 = 1232948;
    PerspectiveEventType type1 = new PerspectiveEventType();
    type1.setPerspectiveId(id);
    type1.setDuration(count1);

    PerspectiveEventListType list1 = new PerspectiveEventListType();
    XMLGregorianCalendar start = toXmlDate(date);
    list1.setDate(start);
    list1.getPerspectiveEvent().add(type1);

    // 2:

    int count2 = 2342817;
    PerspectiveEventType type2 = new PerspectiveEventType();
    type2.setPerspectiveId(id);
    type2.setDuration(count2);

    PerspectiveEventListType list2 = new PerspectiveEventListType();
    XMLGregorianCalendar end = toXmlDate(date);
    list2.setDate(end);
    list2.getPerspectiveEvent().add(type2);

    EventListType events = new EventListType();
    events.getPerspectiveEvents().add(list1);
    events.getPerspectiveEvents().add(list2);

    File f = DataStore.PERSPECTIVE_STORE.getDataFile(date.toLocalDate());
    DataStore.PERSPECTIVE_STORE.write(events, f);

    LocalDate date1 = LocalDate.fromCalendarFields(start.toGregorianCalendar());
    LocalDate date2 = LocalDate.fromCalendarFields(end.toGregorianCalendar());
    assertValues(accessor.getData(date1, date2), events);
  }

  private void assertValues(ImmutableCollection<SessionDataDescriptor> data,
      EventListType events) {

    Map<LocalDate, Long> map = Maps.newHashMap();
    for (PerspectiveEventListType list : events.getPerspectiveEvents()) {
      long value = 0;
      for (PerspectiveEventType type : list.getPerspectiveEvent())
        value += type.getDuration();

      LocalDate date = LocalDate.fromCalendarFields(list.getDate()
          .toGregorianCalendar());
      if (map.containsKey(date))
        map.put(date, value + map.get(date));
      else
        map.put(date, value);
    }

    assertEquals(map.size(), data.size());
    for (SessionDataDescriptor des : data) {
      assertTrue(map.containsKey(des.getDate()));
      assertEquals(map.get(des.getDate()).longValue(), des.getValue());
    }
  }

  @Test(expected = NullPointerException.class)
  public void testGetData_startDateNull() {
    accessor.getData(null, new LocalDate());
  }
}
