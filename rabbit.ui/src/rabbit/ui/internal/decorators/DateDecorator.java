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
package rabbit.ui.internal.decorators;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.joda.time.LocalDate;

public class DateDecorator extends BaseLabelProvider implements ILightweightLabelDecorator {
  
  private LocalDate today;
  private long endOfToday;
  
  public DateDecorator() {
    checkFields();
  }
  
  private void checkFields() {
    if (System.currentTimeMillis() > endOfToday) {
      today = new LocalDate();
      endOfToday = today.toInterval().getEndMillis();
    }
  }

  @Override
  public void decorate(Object element, IDecoration decoration) {
    if (!(element instanceof LocalDate)) {
      return;
    }

    checkFields();
    LocalDate date = (LocalDate) element;
    if (date.getYear() == today.getYear()) {
      switch (today.getDayOfYear() - date.getDayOfYear()) {
      case 0:
        decoration.addSuffix(" [Today]");
        break;
      case 1:
        decoration.addSuffix(" [Yesterday]");
        break;
      default:
        break;
      }
    }
  }
}
