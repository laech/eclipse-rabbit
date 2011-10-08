/*
 * Copyright 2011 The Rabbit Eclipse Plug-in Project
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

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A base page for pages that are composed of multiple {@link IDataPage}s.
 */
public abstract class TabbedPage<T> implements IPage {

  private final Map<IDataPage<T>, Boolean> pageUpdatedStatus;
  private final Map<IDataPage<T>, IContributionItem[]> toolItems;
  private final Map<CTabItem, IDataPage<T>> tabs;

  private CTabFolder folder;
  private IToolBarManager toolBar;
  private Collection<T> data;

  public TabbedPage() {
    pageUpdatedStatus = newLinkedHashMap();
    toolItems = newLinkedHashMap();
    tabs = newLinkedHashMap();
    data = emptyList();
  }

  @Override
  public void createContents(Composite parent) {
    for (IDataPage<T> page : getPages()) {
      pageUpdatedStatus.put(page, FALSE);
    }
    
    folder = new CTabFolder(parent, SWT.BOTTOM);
    folder.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        super.widgetSelected(e);
        IDataPage<T> page = tabs.get(e.item);
        showToolItemsFor(page);
        boolean updated = TRUE.equals(pageUpdatedStatus.get(page));
        if (!updated) {
          page.update(data);
          pageUpdatedStatus.put(page, TRUE);
        }
      }
    });

    for (IDataPage<T> page : pageUpdatedStatus.keySet()) {
      CTabItem tab = new CTabItem(folder, SWT.NONE);
      Composite content = new Composite(folder, SWT.NONE);
      content.setLayout(new FillLayout());
      tab.setText(page.getName());
      page.createContents(content);
      tab.setControl(content);
      tabs.put(tab, page);
    }

//    if (!tabs.isEmpty()) {
//      Entry<CTabItem, IDataPage<T>> entry = tabs.entrySet().iterator().next();
//      folder.setSelection(entry.getKey());
//      showToolItemsFor(entry.getValue());
//    }
    folder.setSelection(0);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    this.toolBar = toolBar;

    for (IDataPage<T> page : pageUpdatedStatus.keySet()) {
      toolItems.put(page, page.createToolBarItems());
    }

    List<IContributionItem> result = newLinkedList();
    for (IContributionItem[] items : toolItems.values()) {
      for (IContributionItem item : items) {
        result.add(item);
        toolBar.add(item);
      }
    }

    return result.toArray(new IContributionItem[result.size()]);
  }

  @Override
  public void onRestoreState(IMemento memento) {
    for (IDataPage<T> page : pageUpdatedStatus.keySet()) {
      page.onRestoreState(memento);
    }
  }

  @Override
  public void onSaveState(IMemento memento) {
    for (IDataPage<T> page : pageUpdatedStatus.keySet()) {
      page.onSaveState(memento);
    }
  }

  /**
   * Gets the child page objects for this composite page.
   * 
   * @return the child pages.
   */
  protected abstract IDataPage<T>[] getPages();

  /**
   * Subclass should call this method when their data changes and the UI should
   * be updated.
   * 
   * @param data the new data, not {@code null}
   */
  protected void update(final Collection<T> data) {
    this.data = data;
    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
      @Override
      public void run() {
        for (Entry<IDataPage<T>, Boolean> entry : pageUpdatedStatus.entrySet()) {
          entry.setValue(FALSE);
        }

        CTabItem selected = folder.getSelection();
        if (selected == null) {
          return;
        }

        IDataPage<T> page = tabs.get(selected);
        page.update(data);
        pageUpdatedStatus.put(page, TRUE);
      }
    });
  }

  private void showToolItemsFor(IDataPage<T> page) {
    for (Entry<IDataPage<T>, IContributionItem[]> entry : toolItems.entrySet()) {
      boolean visible = entry.getKey().equals(page);
      for (IContributionItem item : entry.getValue()) {
        item.setVisible(visible);
      }
    }
    toolBar.update(true);
  }
}
