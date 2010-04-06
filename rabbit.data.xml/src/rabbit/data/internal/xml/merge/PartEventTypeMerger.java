/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.PartEventType;

import com.google.common.base.Objects;

/**
 * Merger for {@link PartEventType}.
 */
public class PartEventTypeMerger extends AbstractMerger<PartEventType> {

  @Override
  protected boolean doIsMergeable(PartEventType t1, PartEventType t2) {
    return Objects.equal(t1.getPartId(), t2.getPartId());
  }

  @Override
  protected PartEventType doMerge(PartEventType t1, PartEventType t2) {
    PartEventType result = new PartEventType();
    result.setPartId(t1.getPartId());
    result.setDuration(t1.getDuration() + t2.getDuration());
    return result;
  }

}
