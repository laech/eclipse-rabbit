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
package rabbit.data.test.xml.store;

import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.test.xml.AbstractStorerTest;
import rabbit.data.xml.store.SessionEventStorer;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @see SessionEventStorer
 */
@SuppressWarnings("restriction")
public class SessionEventStorerTest extends
    AbstractStorerTest<SessionEvent, SessionEventType, SessionEventListType> {

  @Override
  protected SessionEvent createEvent(DateTime dateTime) throws Exception {
    return new SessionEvent(dateTime, 12);
  }

  @Override
  protected SessionEvent createEventDiff(DateTime dateTime) throws Exception {
    return new SessionEvent(dateTime, 129834);
  }

  @Override
  protected SessionEventStorer createStorer() {
    return SessionEventStorer.getInstance();
  }

  @Override
  protected boolean equal(SessionEventType t1, SessionEventType t2) {
    return t1.getDuration() == t2.getDuration();
  }

  @Test
  @Override
  @Ignore("Does not apply, all SessionEventTypes are mergeable")
  public void testInsert_withNonConvertableElements() throws Exception {
    super.testInsert_withNonConvertableElements();
  }
}