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

import rabbit.data.access.IAccessor2;
import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preferences;
import rabbit.ui.TreeViewerLabelSorter;
import rabbit.ui.TreeViewerSorter;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByDatesAction;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.joda.time.LocalDate;

/**
 * A page for displaying command usage.
 */
public class CommandPage extends AbstractTreeViewerPage {

  /**
   * Preference constants for displaying the data by date.
   */
  private static final String DISPLAY_BY_DATE_PREF = "PartPage.displayByDates";

  private final IAccessor2<CommandDataDescriptor> accessor;
  private final CommandPageContentProvider contents;
  private final CommandPageLabelProvider labels;

  /**
   * Constructor.
   */
  public CommandPage() {
    super();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
    store.setDefault(DISPLAY_BY_DATE_PREF, true);

    boolean displayByDate = store.getBoolean(DISPLAY_BY_DATE_PREF);
    contents = new CommandPageContentProvider(this, displayByDate);
    labels = new CommandPageLabelProvider(contents);
    accessor = DataHandler.getCommandDataAccessor();
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.addSelectionListener(createInitialComparator(viewer));
    column.setText("Name");
    column.setWidth(150);

    column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.addSelectionListener(new TreeViewerLabelSorter(viewer));
    column.setText("Description");
    column.setWidth(200);

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.addSelectionListener(createValueSorterForTree(viewer));
    column.setText("Usage Count");
    column.setWidth(100);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(new ExpandAllAction(getViewer())),
        new ActionContributionItem(new CollapseAllAction(getViewer())),
        new Separator(),// 
        new ActionContributionItem(new GroupByDatesAction(contents)) };

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  @Override
  public long getValue(Object o) {
    if (o instanceof Command)
      return contents.getValueOfCommand((Command) o);

    if (o instanceof CommandDataDescriptor)
      return ((CommandDataDescriptor) o).getValue();

    return 0;
  }

  @Override
  public void update(Preferences p) {
    setMaxValue(0);
    labels.updateState();

    Object[] elements = getViewer().getExpandedElements();
    ISelection selection = getViewer().getSelection();

    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    getViewer().setInput(accessor.getData(start, end));
    try {
      getViewer().setExpandedElements(elements);
      getViewer().setSelection(selection);
    } catch (Exception e) {
      // Just in case something goes wrong while restoring the viewer's state
    }
  }

  @Override
  protected ITreeContentProvider createContentProvider() {
    return contents;
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof LocalDate && e2 instanceof LocalDate)
          return ((LocalDate) e1).compareTo((LocalDate) e2);

        if (e1 instanceof Command && e2 instanceof Command)
          return compareCommands((Command) e1, (Command) e2);

        if (e1 instanceof CommandDataDescriptor
            && e2 instanceof CommandDataDescriptor) {
          Command cmd1 = contents.getCommand((CommandDataDescriptor) e1);
          Command cmd2 = contents.getCommand((CommandDataDescriptor) e2);
          return compareCommands(cmd1, cmd2);
        }

        return 0;
      }

      private int compareCommands(Command e1, Command e2) {
        String a = ((Command) e1).getId();
        String b = ((Command) e2).getId();
        try {
          a = ((Command) e1).getName();
        } catch (NotDefinedException e) {
        }
        try {
          b = ((Command) e2).getName();
        } catch (NotDefinedException e) {
        }
        return a.compareToIgnoreCase(b);
      }
    };
  }

  @Override
  protected ITableLabelProvider createLabelProvider() {
    return labels;
  }

  @Override
  protected void saveState() {
    super.saveState();
    RabbitUI.getDefault().getPreferenceStore().setValue(DISPLAY_BY_DATE_PREF,
        contents.isDisplayingByDate());
  }
}
