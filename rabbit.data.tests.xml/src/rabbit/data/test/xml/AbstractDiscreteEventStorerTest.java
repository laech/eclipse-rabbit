/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.data.test.xml;

import rabbit.data.internal.xml.AbstractDiscreteEventStorer;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;
import rabbit.data.store.model.DiscreteEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test for {@link AbstractDiscreteEventStorer}
 */
public abstract class AbstractDiscreteEventStorerTest<E extends DiscreteEvent, T, S extends EventGroupType>
    extends AbstractStorerTest<E, T, S> {

  protected AbstractDiscreteEventStorer<E, T, S> storer = create();

  protected File dataFile;

  public AbstractDiscreteEventStorerTest() {
    try {
      dataFile = getDataStore(storer).getDataFile(Calendar.getInstance());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Calls the protected method {@code AbstractDiscreteEventStorer.merge(T, T)}
   */
  protected void merge(AbstractDiscreteEventStorer<E, T, S> storer, T t1, T t2)
      throws Exception {
    Method method = AbstractDiscreteEventStorer.class.getDeclaredMethod(
        "merge", Object.class, Object.class);
    method.setAccessible(true);
    method.invoke(storer, t1, t2);
  }

  @Before
  public void setUp() throws Exception {
    if (dataFile.exists()) {
      if (!dataFile.delete()) {
        System.err.println("File is not deleted.");
      }
    }
    getDataField(storer).clear();
  }

  @Test
  public void testAbstractXmlStorer() {
    assertNotNull(storer);
  }

  @Test
  public void testGetDataFile() throws Exception {
    assertNotNull(getDataStore(storer).getDataFile(Calendar.getInstance()));
  }

  @Test
  public void testGetDataFiles() throws Exception {

    Calendar lowerBound = new GregorianCalendar(1, 1, 1);
    Calendar upperBound = new GregorianCalendar(3, 1, 1);

    Calendar insideLowerBound = (Calendar) lowerBound.clone();
    insideLowerBound.add(Calendar.MONTH, 1);

    Calendar insideUpperBound = (Calendar) upperBound.clone();
    insideUpperBound.add(Calendar.MONTH, -1);

    Set<File> files = new HashSet<File>();
    files.add(getDataStore(storer).getDataFile(lowerBound));
    files.add(getDataStore(storer).getDataFile(upperBound));
    files.add(getDataStore(storer).getDataFile(insideLowerBound));
    files.add(getDataStore(storer).getDataFile(insideUpperBound));
    for (File f : files) {
      if (!f.createNewFile() && !f.exists()) {
        throw new RuntimeException();
      }
    }

    List<File> returnedFiles = getDataStore(storer).getDataFiles(lowerBound,
        upperBound);
    assertEquals(files.size(), returnedFiles.size());
    for (File f : returnedFiles) {
      assertTrue(files.contains(f));
    }

    assertEquals(0, getDataStore(storer).getDataFiles(upperBound, lowerBound)
        .size());

    for (File f : files) {
      if (!f.delete()) {
        System.err.println("File is not deleted.");
      }
    }
    assertEquals(0, getDataStore(storer).getDataFiles(lowerBound, upperBound)
        .size());
  }

  @Test
  public abstract void testHasSameId_typeAndType() throws Exception;

  @Test
  public abstract void testMerge_typeAndType() throws Exception;

  @Test
  public abstract void testNewXmlType() throws Exception;

  @Test
  public void testRead() throws Exception {

    Calendar cal = new GregorianCalendar(1, 1, 1);
    File f = getDataStore(storer).getDataFile(cal);

    if (f.exists()) {
      assertNotNull(getDataStore(storer).read(f));

    } else {
      if (!f.createNewFile()) {
        throw new RuntimeException();
      }
      assertNotNull(getDataStore(storer).read(f));
      if (!f.delete()) {
        System.err.println("File is not deleted.");
      }
    }
  }

  @Test
  public void testWrite() throws Exception {

    File f = new File(System.getProperty("user.home") + File.separator
        + "tmpTestFile.xml");
    assertFalse(f.exists());

    getDataStore(storer).write(new ObjectFactory().createEventListType(), f);
    assertTrue(f.exists());
    if (!f.delete()) {
      System.err.println("File is not deleted.");
    }
  }

  @Override
  protected abstract AbstractDiscreteEventStorer<E, T, S> create();

  /** Creates an event (with all values filled) for testing. */
  protected abstract E createEvent();

  /**
   * Creates an event (with all values filled) that is different to
   * {@link #createEvent()}.
   */
  protected abstract E createEvent2();

  /**
   * Calls the protected method {@code AbstractDiscreteEventStorer.hasSameId(T,
   * T)}.
   */
  protected boolean hasSameId(AbstractDiscreteEventStorer<E, T, S> storer,
      T t1, T t2) throws Exception {
    Method method = AbstractDiscreteEventStorer.class.getDeclaredMethod(
        "hasSameId", Object.class, Object.class);
    method.setAccessible(true);
    return (Boolean) method.invoke(storer, t1, t2);
  }

  /**
   * Calls the protected method (@code
   * AbstractDiscreteEventStorer.newXmlType(E)}.
   */
  @SuppressWarnings("unchecked")
  protected T newXmlType(AbstractDiscreteEventStorer storer, E event)
      throws Exception {
    Method method = AbstractDiscreteEventStorer.class.getDeclaredMethod(
        "newXmlType", DiscreteEvent.class);
    method.setAccessible(true);
    return (T) method.invoke(storer, event);
  }
}
