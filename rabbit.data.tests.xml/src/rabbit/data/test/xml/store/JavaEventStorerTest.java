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

import rabbit.data.internal.xml.schema.events.JavaEventListType;
import rabbit.data.internal.xml.schema.events.JavaEventType;
import rabbit.data.internal.xml.store.JavaEventStorer;
import rabbit.data.store.model.JavaEvent;
import rabbit.data.test.xml.AbstractStorerTest;

import org.eclipse.jdt.core.JavaCore;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * @see JavaEventStorer
 */
@SuppressWarnings("restriction")
public class JavaEventStorerTest extends
    AbstractStorerTest<JavaEvent, JavaEventType, JavaEventListType> {

  @Override
  protected JavaEvent createEvent(DateTime dateTime) throws Exception {
    return new JavaEvent(new Interval(dateTime, dateTime.plus(1)),
        JavaCore.create("=Enfo/src<enfo{EnfoPlugin.java"));
  }

  @Override
  protected JavaEvent createEventDiff(DateTime dateTime) throws Exception {
    return new JavaEvent(new Interval(dateTime, dateTime.plus(2)),
        JavaCore.create("=Proj/src<pkg{File.java"));
  }

  @Override
  protected JavaEventStorer createStorer() {
    return JavaEventStorer.getInstance();
  }

  @Override
  protected boolean equal(JavaEventType t1, JavaEventType t2) {
    return t1.getDuration() == t2.getDuration()
        && t1.getHandleIdentifier().equals(t2.getHandleIdentifier());
  }

}
