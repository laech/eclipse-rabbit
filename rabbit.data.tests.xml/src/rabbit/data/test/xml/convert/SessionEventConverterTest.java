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
package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.AbstractConverter;
import rabbit.data.internal.xml.convert.SessionEventConverter;
import rabbit.data.internal.xml.schema.events.SessionEventType;
import rabbit.data.store.model.SessionEvent;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;

/**
 * @see SessionEventConverter
 */
@SuppressWarnings("restriction")
public class SessionEventConverterTest extends
    AbstractConverterTest<SessionEvent, SessionEventType> {

  @Override
  protected AbstractConverter<SessionEvent, SessionEventType> createConverter() {
    return new SessionEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    SessionEvent event = new SessionEvent(new DateTime(), 101);
    SessionEventType type = converter.convert(event);
    assertEquals(event.getDuration(), type.getDuration());
    
    event = new SessionEvent(new DateTime(), 198347);
    type = converter.convert(event);
    assertEquals(event.getDuration(), type.getDuration());
  }

}
