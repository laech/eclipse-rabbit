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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

// TODO
public class DelegatingStyledCellLabelProvider extends StyledCellLabelProvider {
  
  private final ILabelProvider labelProvider;
  private final boolean shouldDispose;
  
  public DelegatingStyledCellLabelProvider(ILabelProvider labelProvider, boolean shouldDispose) {
    checkNotNull(labelProvider);
    this.labelProvider = labelProvider;
    this.shouldDispose = shouldDispose;
  }
  
  @Override
  public void update(ViewerCell cell) {
    super.update(cell);
    cell.setText(labelProvider.getText(cell.getElement()));
    cell.setImage(labelProvider.getImage(cell.getElement()));
    if (labelProvider instanceof IFontProvider) {
      cell.setFont(((IFontProvider) labelProvider).getFont(cell.getText()));
    }
    if (labelProvider instanceof IColorProvider) {
      cell.setForeground(((IColorProvider) labelProvider).getForeground(cell.getElement()));
      cell.setBackground(((IColorProvider) labelProvider).getBackground(cell.getElement()));
    }
  }
  
  @Override
  public void dispose() {
    super.dispose();
    if (shouldDispose) {
      labelProvider.dispose();
    }
  }

  @Override
  protected void measure(Event event, Object element) {
    super.measure(event, element);
    if (event.height < 19) {
      event.height = 19;
    }
  }
}
