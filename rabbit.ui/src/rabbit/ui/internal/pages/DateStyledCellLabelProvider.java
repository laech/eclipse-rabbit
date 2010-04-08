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

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.joda.time.LocalDate;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * A {@code StyledCellLabelProvider} that providers labels and images for
 * {@link LocalDate}
 */
public class DateStyledCellLabelProvider extends StyledCellLabelProvider {

  private final LocalDateLabelProvider labels;
  private LocalDate today;

  public DateStyledCellLabelProvider() {
    labels = new LocalDateLabelProvider();
    today = new LocalDate();
  }

  @Override
  public void dispose() {
    super.dispose();
    labels.dispose();
  }

  @Override
  public void update(ViewerCell cell) {
    super.update(cell);

    Object element = cell.getElement();
    if (element instanceof LocalDate) {
      LocalDate date = (LocalDate) element;

      StyledString styledString = new StyledString(labels.getText(date));
      if (date.isEqual(today))
        styledString.append(" [Today]", StyledString.DECORATIONS_STYLER);

      else if (date.getYear() == today.getYear()
          && date.getDayOfYear() == today.getDayOfYear() - 1)
        styledString.append(" [Yestoday]", StyledString.DECORATIONS_STYLER);

      cell.setText(styledString.getString());
      cell.setStyleRanges(styledString.getStyleRanges());
      cell.setImage(labels.getImage(date));

    } else {
      cell.setText(null);
      cell.setImage(null);
    }
  }

  /**
   * Updates the state of this label provider, this method should be called when
   * the input of the viewer is changed.
   */
  @OverridingMethodsMustInvokeSuper
  public void updateState() {
    today = new LocalDate();
  }
}
