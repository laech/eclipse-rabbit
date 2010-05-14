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
package rabbit.ui.internal.pages;

import rabbit.ui.internal.SharedImages;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Label provider for {@link LocalDate} elements.
 */
public class DateLabelProvider extends LabelProvider {

  private final Image dateImage;
  private final DateTimeFormatter formatter;

  /**
   * Variable indicates the end of today in milliseconds, if
   * {@link System#currentTimeMillis()} is greater than this value, then
   * tomorrow is now.
   */
  private long todayEnd;
  private LocalDate today;

  /**
   * Constructor.
   */
  public DateLabelProvider() {
    formatter = DateTimeFormat.longDate();
    dateImage = SharedImages.CALENDAR.createImage();
    today = new LocalDate();
    todayEnd = today.toInterval().getEndMillis();
  }

  @Override
  public Image getImage(@Nullable Object element) {
    if (element instanceof LocalDate)
      return dateImage;
    else
      return null;
  }

  @Override
  public String getText(@Nullable Object element) {
    checkTime();
    if (element instanceof LocalDate) {
      LocalDate date = (LocalDate) element;
      String text = formatter.print(date);
      if (date.isEqual(today))
        return text + " - Today";
      else if (date.getYear() == today.getYear()
          && date.getDayOfYear() == today.getDayOfYear() - 1)
        return text + " - Yesterday";
      else
        return text;
    }
    return null;
  }

  @Override
  public void dispose() {
    super.dispose();
    dateImage.dispose();
  }
  
  /**TODO
   * Checks the current time and update if needed.
   */
  private void checkTime() {
    if (System.currentTimeMillis() > todayEnd) {
      today = new LocalDate();
      todayEnd = today.toInterval().getEndMillis();
    }
  }

  /**TODO
   * Updates the state of this label provider, this method should be called when
   * the input of the viewer is changed.
   */
  @OverridingMethodsMustInvokeSuper
  public void updateState() {
    today = new LocalDate();
  }
}
