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
package rabbit.ui;

import rabbit.ui.internal.RabbitUI;

import java.util.Calendar;

/**
 * Contains preferences for displaying data.
 */
public final class DisplayPreference {

  private Calendar startDate;
  private Calendar endDate;

  /** Constructor. */
  public DisplayPreference() {
    endDate = Calendar.getInstance();
    startDate = (Calendar) endDate.clone();
    startDate.add(Calendar.DAY_OF_MONTH, -RabbitUI.getDefault()
        .getDefaultDisplayDatePeriod());
  }

  /**
   * Gets the end date for the data to be displayed.
   * 
   * @return The end date.
   */
  public Calendar getEndDate() {
    return endDate;
  }

  /**
   * Gets the start date for the data to be displayed.
   * 
   * @return The start date.
   */
  public Calendar getStartDate() {
    return startDate;
  }
}
