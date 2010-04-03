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
package rabbit.data.internal.xml;

import rabbit.data.internal.xml.schema.events.DurationEventType;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.store.model.ContinuousEvent;

/**
 * Stores events who's super class is {@link ContinuousEvent}.
 * 
 * @param <E> A {@link ContinuousEvent}.
 * @param <T> A {@link DurationEventType}.
 * @param <S> A {@link EventGroupType}.
 */
public abstract class AbstractContinuousEventStorer<E extends ContinuousEvent, T extends DurationEventType, S extends EventGroupType>
    extends AbstractDiscreteEventStorer<E, T, S> {

  @Override
  protected void merge(T main, T x) {
    main.setDuration(main.getDuration() + x.getDuration());
  }
}
