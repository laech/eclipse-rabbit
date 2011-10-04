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

import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.WORKSPACE;
import static rabbit.ui.internal.viewers.Viewers.newFilteredTree;
import static rabbit.ui.internal.viewers.Viewers.newTreeViewerColumn;
import static rabbit.ui.internal.viewers.Viewers.refresh;
import static rabbit.ui.internal.viewers.Viewers.resetInput;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preference;
import rabbit.ui.internal.treebuilders.SessionDataTreeBuilder;
import rabbit.ui.internal.treebuilders.SessionDataTreeBuilder.ISessionDataProvider;
import rabbit.ui.internal.util.Categorizer;
import rabbit.ui.internal.util.CategoryProvider;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.IConverter;
import rabbit.ui.internal.util.SimpleVisualProvider;
import rabbit.ui.internal.util.TreePathDurationConverter;
import rabbit.ui.internal.util.TreePathValueProvider;
import rabbit.ui.internal.viewers.CompositeCellLabelProvider;
import rabbit.ui.internal.viewers.DateLabelProvider;
import rabbit.ui.internal.viewers.FilterableTreePathContentProvider;
import rabbit.ui.internal.viewers.TreePathContentProvider;
import rabbit.ui.internal.viewers.TreePathDurationLabelProvider;
import rabbit.ui.internal.viewers.TreePathPatternFilter;
import rabbit.ui.internal.viewers.TreeViewerCellPainter;
import rabbit.ui.internal.viewers.TreeViewerColumnSorter;
import rabbit.ui.internal.viewers.TreeViewerColumnValueSorter;
import rabbit.ui.internal.viewers.WorkspaceStorageLabelProvider;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import static org.jfree.chart.ChartFactory.createGanttChart;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public final class SessionPage extends SaveStateViewerPage {

  private FilteredTree tree;
  private CategoryProvider viewerCategories;
  private TreePathValueProvider viewerValues;
  private TreePathContentProvider viewerContents;
  private ChartComposite chartComposite;
  private SimpleVisualProvider chartVisualProvider;

  private List<IContributionItem> viewerItems;
  private List<IContributionItem> chartItems;
  private IToolBarManager toolBar;
  
  private Collection<ISessionData> data;
  private Preference pref;

  public SessionPage() {
  }

  @Override
  public void createContents(Composite parent) {
    CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);

    final CTabItem viewerTab = new CTabItem(folder, SWT.NONE);
    viewerTab.setText("Viewer");
    createTabForViewer(folder, viewerTab);
    folder.setSelection(viewerTab);

    final CTabItem chartTab = new CTabItem(folder, SWT.NONE);
    chartTab.setText("Chart");
    chartTab.setControl(createTabForChart(folder));

    folder.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        super.widgetSelected(e);
        if (viewerTab.equals(e.item)) {
          showOnlyViewerToolbarItems();
        } else if (chartTab.equals(e.item)) {
          showOnlyChartToolbarItems();
        }
      }
    });
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    this.toolBar = toolBar;
    viewerItems = new CommonToolBarBuilder()
        .enableFilterControlAction(tree, true)
        .enableTreeAction(tree.getViewer())
        .enableGroupByAction(viewerCategories)
        .enableColorByAction(viewerValues)
        .addGroupByAction(DATE)
        .addGroupByAction(WORKSPACE, DATE)
        .addColorByAction(DATE)
        .addColorByAction(WORKSPACE)
        .build();

    chartVisualProvider = new SimpleVisualProvider();
    chartVisualProvider.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        JFreeChart chart = createChart2(
            new LocalDate(pref.getStartDate().getTimeInMillis()),
            new LocalDate(pref.getEndDate().getTimeInMillis()));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(208, 145, 60));
        ((IntervalBarRenderer)renderer).setShadowVisible(false);
        ((IntervalBarRenderer)renderer)
            .setBarPainter(new StandardBarPainter());
        chartComposite.setChart(chart);
        chartComposite.redraw();
      }
    });
    chartItems = new CommonToolBarBuilder()
        .enableColorByAction(chartVisualProvider)
        .addColorByAction(DATE)
        .addColorByAction(WORKSPACE)
        .build();

    for (IContributionItem item : viewerItems) {
      toolBar.add(item);
    }
    for (IContributionItem item : chartItems) {
      toolBar.add(item);
    }

    return viewerItems.toArray(new IContributionItem[viewerItems.size()]);
  }

  @Override
  public Job updateJob(final Preference pref) {
    this.pref = pref;
    TreeViewer viewer = tree.getViewer();
    return new UpdateJob<ISessionData>(viewer, pref, getAccessor()) {
      @Override
      protected Object getInput(final Collection<ISessionData> data) {
        return new ISessionDataProvider() {
          @Override
          public Collection<ISessionData> get() {
            SessionPage.this.data = data;
            JFreeChart chart = createChart2(
                new LocalDate(pref.getStartDate().getTimeInMillis()),
                new LocalDate(pref.getEndDate().getTimeInMillis()));
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            CategoryItemRenderer renderer = plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(208, 145, 60));
            ((IntervalBarRenderer)renderer).setShadowVisible(false);
            ((IntervalBarRenderer)renderer)
                .setBarPainter(new StandardBarPainter());
            chartComposite.setChart(chart);
            chartComposite.redraw();
            return data;
          }
        };
      }
    };
  }

  @Override
  protected TreeColumn[] getColumns() {
    return tree.getViewer().getTree().getColumns();
  }

  @Override
  protected Category[] getSelectedCategories() {
    return viewerCategories.getSelected().toArray(new Category[0]);
  }

  @Override
  protected Category getVisualCategory() {
    return (Category)viewerValues.getVisualCategory();
  }

  @Override
  protected void setSelectedCategories(List<Category> categories) {
    this.viewerCategories.setSelected(categories.toArray(
        new Category[categories.size()]));
  }

  @Override
  protected void setVisualCategory(Category category) {
    viewerValues.setVisualCategory(category);
  }

  private JFreeChart createChart2(LocalDate start, LocalDate end) {
    ICategory category = chartVisualProvider.getVisualCategory();
    Map<Object, Task> tasks = Maps.newHashMap();
    TaskSeries serise = new TaskSeries("Unavailable");
    for (ISessionData d : data) {
      List<Interval> intervals = d.get(ISessionData.INTERVALS);
      if (!intervals.isEmpty()) {
        Object key;
        String name;
        if (DATE.equals(category)) {
          LocalDate date = d.get(ISessionData.DATE);
          key = date;
          name = date.toString();

        } else if (WORKSPACE.equals(category)) {
          WorkspaceStorage ws = d.get(ISessionData.WORKSPACE);
          key = ws;
          IPath path = ws.getWorkspacePath();
          if (path != null) {
            name = path.lastSegment();
          } else {
            name = ws.getStoragePath().toOSString();
          }

        } else {
          continue;
        }

        Task task = tasks.get(key);
        if (task == null) {
          task = new Task(name,
              start.toDateMidnight().toDate(),
              end.toDateMidnight().plusDays(1).toDate());
          tasks.put(key, task);
        }
        serise.add(task);
        System.err.println(intervals.size());
        for (Interval interval : intervals) {
          task.addSubtask(new Task(interval.toString(),
              interval.getStart().toDate(),
              interval.getEnd().toDate()));
        }
      }
    }

    TaskSeriesCollection dataset = new TaskSeriesCollection();
    dataset.add(serise);

    return createGanttChart(null, null, null, dataset, false, true, false);
  }

  private Composite createTabForChart(CTabFolder folder) {
    TaskSeriesCollection dataset = new TaskSeriesCollection();
    JFreeChart chart = ChartFactory.createGanttChart(null, null, null, dataset,
        true, true, true);
    chartComposite = new ChartComposite(folder, SWT.NONE, chart, true);
    return chartComposite;
  }

  private void createTabForViewer(CTabFolder folder, CTabItem tab) {
    viewerCategories = createViewerCategoryProvider();
    viewerContents = createViewerContentProvider();
    viewerValues = createViewerValueProvider();

    // The main label provider for the first column:
    CompositeCellLabelProvider nameLabels = new CompositeCellLabelProvider(
        new DateLabelProvider(), new WorkspaceStorageLabelProvider());

    // The viewer:
    tree = newFilteredTree(folder, new TreePathPatternFilter(nameLabels));
    tab.setControl(tree);
    TreeViewer viewer = tree.getViewer();
    FilterableTreePathContentProvider filteredContentProvider =
        new FilterableTreePathContentProvider(viewerContents);
    filteredContentProvider.addFilter(instanceOf(Duration.class));
    viewer.setContentProvider(filteredContentProvider);

    // Column sorters:
    TreeViewerColumnSorter labelSorter =
        new InternalTreeViewerColumnLabelSorter(viewer, nameLabels);
    TreeViewerColumnSorter durationSorter =
        new TreeViewerColumnValueSorter(viewer, viewerValues);

    // The columns:

    TreeViewerColumn name = newTreeViewerColumn(viewer, SWT.LEFT);
    name.getColumn().setText("Name");
    name.getColumn().setWidth(200);
    name.getColumn().addSelectionListener(labelSorter);
    name.setLabelProvider(new DecoratingStyledCellLabelProvider(
        nameLabels, getPlatformLabelDecorator(), null));

    TreeViewerColumn duration = newTreeViewerColumn(viewer, SWT.RIGHT);
    duration.getColumn().setText("Duration");
    duration.getColumn().setWidth(150);
    duration.getColumn().addSelectionListener(durationSorter);
    duration.setLabelProvider(new TreePathDurationLabelProvider(viewerValues));

    TreeViewerColumn graph = newTreeViewerColumn(viewer, SWT.LEFT);
    graph.getColumn().setWidth(100);
    graph.getColumn().addSelectionListener(durationSorter);
    graph.setLabelProvider(TreeViewerCellPainter.observe(viewerValues,
        viewerValues, new RGB(208, 145, 60)));
  }

  private CategoryProvider createViewerCategoryProvider() {
    Category[] supported = new Category[]{WORKSPACE, DATE};
    CategoryProvider provider = new CategoryProvider(supported, DATE);
    provider.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        resetInput(tree.getViewer());
      }
    });
    return provider;
  }

  private TreePathContentProvider createViewerContentProvider() {
    TreePathContentProvider provider = new TreePathContentProvider(
        new SessionDataTreeBuilder(viewerCategories));
    provider.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        viewerValues.setVisualCategory(viewerValues.getVisualCategory());
      }
    });
    return provider;
  }

  private TreePathValueProvider createViewerValueProvider() {
    Map<Predicate<Object>, Category> categories = ImmutableMap.of(
        instanceOf(LocalDate.class), DATE,
        instanceOf(WorkspaceStorage.class), WORKSPACE);
    ICategorizer categorizer = new Categorizer(categories);
    IConverter<TreePath> converter = new TreePathDurationConverter();
    TreePathValueProvider provider = new TreePathValueProvider(categorizer,
        viewerContents, converter, DATE);
    provider.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        refresh(tree.getViewer());
      }
    });
    return provider;
  }

  private IAccessor<ISessionData> getAccessor() {
    return DataHandler.getAccessor(ISessionData.class);
  }

  private ILabelDecorator getPlatformLabelDecorator() {
    return PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  private void showOnlyChartToolbarItems() {
    for (IContributionItem item : chartItems) {
      item.setVisible(true);
    }
    for (IContributionItem item : viewerItems) {
      item.setVisible(false);
    }
    toolBar.update(true);
  }

  private void showOnlyViewerToolbarItems() {
    for (IContributionItem item : viewerItems) {
      item.setVisible(true);
    }
    for (IContributionItem item : chartItems) {
      item.setVisible(false);
    }
    toolBar.update(true);
  }
}
