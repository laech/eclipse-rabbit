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
package rabbit.data.store.model;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.DateTime;

/**
 * Represents a perspective event.
 */
public class PerspectiveEvent extends ContinuousEvent {

  private IPerspectiveDescriptor perspective;

  /**
   * Constructs a perspective event.
   * 
   * @param endTime The end time of the event.
   * @param duration The duration in milliseconds.
   * @param perspective The perspective.
   * @throws NullPointerException If time is null or perspective is null.
   * @throws IllegalArgumentException If duration is negative.
   */
  public PerspectiveEvent(DateTime endTime, long duration,
      IPerspectiveDescriptor perspective) {
    
    super(endTime, duration);
    if (perspective == null)
      throw new NullPointerException("Perspective cannot be null");

    this.perspective = perspective;
  }

  /**
   * Gets the perspective.
   * 
   * @return The perspective.
   */
  public IPerspectiveDescriptor getPerspective() {
    return perspective;
  }
}
