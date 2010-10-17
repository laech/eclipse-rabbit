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

/**
 * A cell label provider that updates the content of the cell using an
 * {@link ILabelProvider}.
 */
public class DelegatingStyledCellLabelProvider extends StyledCellLabelProvider {
  
  /*
   * Using a cell label provider instead of a normal label provider can take
   * care of some selection issues. For example, on Window Vista/7, when a tree
   * item is selected when using a normal label provider, the spacing between
   * left and right is uneven (doesn't look good).
   */
  
  private final ILabelProvider labelProvider;
  private final boolean shouldDispose;

  /**
   * Constructs a new label provider.
   * 
   * @param labelProvider The actual label provider to get the elements' labels.
   *          Can also be an {@link IFontProvider} and/or an
   *          {@link IColorProvider}.
   * @param shouldDispose If true, the given label provider will be disposed
   *          when this object is disposed. If false, the given label provider
   *          will not be disposed when this object is disposed.
   */
  public DelegatingStyledCellLabelProvider(ILabelProvider labelProvider, boolean shouldDispose) {
    checkNotNull(labelProvider);
    this.labelProvider = labelProvider;
    this.shouldDispose = shouldDispose;
  }
  
  @Override
  public void update(ViewerCell cell) {
    super.update(cell);
    Object element = cell.getElement();
    cell.setText(labelProvider.getText(element));
    cell.setImage(labelProvider.getImage(element));
    if (labelProvider instanceof IFontProvider) {
      cell.setFont(((IFontProvider) labelProvider).getFont(element));
    }
    if (labelProvider instanceof IColorProvider) {
      cell.setForeground(((IColorProvider) labelProvider).getForeground(element));
      cell.setBackground(((IColorProvider) labelProvider).getBackground(element));
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
    if (event.height < 20) {
      event.height = 20;
    }
  }
}
