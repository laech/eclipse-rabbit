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

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.DateTime;

/**
 * Represents a workbench part event.
 */
public class PartEvent extends ContinuousEvent {

  private IWorkbenchPart workbenchPart;

  /**
   * Constructs a new event.
   * 
   * @param endTime The end time of the event.
   * @param duration The duration of the event, in milliseconds.
   * @param part The workbench part.
   * @throws NullPointerException If time is null or part is null.
   * @throws IllegalArgumentException If duration is negative.
   */
  public PartEvent(DateTime endTime, long duration, IWorkbenchPart part) {
    super(endTime, duration);
    if (part == null)
      throw new NullPointerException("WorkbenchPart cannot be null");
    
    this.workbenchPart = part;
  }

  /**
   * Gets the workbench part.
   * 
   * @return The workbench part.
   */
  public IWorkbenchPart getWorkbenchPart() {
    return workbenchPart;
  }
}
