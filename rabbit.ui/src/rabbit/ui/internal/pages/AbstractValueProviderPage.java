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

import rabbit.ui.IPage;
import rabbit.ui.TableLabelComparator;
import rabbit.ui.TreeLabelComparator;
import rabbit.ui.CellPainter.IValueProvider;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public abstract class AbstractValueProviderPage implements IValueProvider,
    IPage {

  private long maxValue = 0;

  public AbstractValueProviderPage() {
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    return new IContributionItem[0];
  }

  @Override
  public long getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(long value) {
    maxValue = value;
  }

  // TODO
  protected TableLabelComparator createValueSorterForTable(TableViewer viewer) {
    return new TableLabelComparator(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        return (getValue(e1) > getValue(e2)) ? 1 : -1;
      }
    };
  }
  // TODO
  protected TreeLabelComparator createValueSorterForTree(TreeViewer viewer) {
    return new TreeLabelComparator(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        return (getValue(e1) > getValue(e2)) ? 1 : -1;
      }
    };
  }
}
