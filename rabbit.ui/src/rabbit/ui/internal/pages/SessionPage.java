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
import rabbit.ui.internal.viewers.Viewers;
import rabbit.ui.internal.viewers.WorkspaceStorageLabelProvider;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Maps.newHashMap;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import static org.jfree.chart.ChartFactory.createGanttChart;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.WHITE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public final class SessionPage extends TabbedPage<ISessionData> {

  private static final class ViewerPage
      extends SaveStateViewerDataPage<ISessionData> {

    private FilteredTree tree;
    private CategoryProvider categories;
    private TreePathValueProvider values;
    private TreePathContentProvider contents;

    @Override
    public void createContents(Composite parent) {
      categories = createCategoryProvider();
      contents = createContentProvider();
      values = createValueProvider();

      // The main label provider for the first column:
      CompositeCellLabelProvider nameLabels = new CompositeCellLabelProvider(
          new DateLabelProvider(), new WorkspaceStorageLabelProvider());

      // The viewer:
      tree = newFilteredTree(parent, new TreePathPatternFilter(nameLabels));
      TreeViewer viewer = tree.getViewer();
      FilterableTreePathContentProvider filteredContentProvider =
          new FilterableTreePathContentProvider(contents);
      filteredContentProvider.addFilter(instanceOf(Duration.class));
      viewer.setContentProvider(filteredContentProvider);

      // Column sorters:
      TreeViewerColumnSorter labelSorter =
          new InternalTreeViewerColumnLabelSorter(viewer, nameLabels);
      TreeViewerColumnSorter durationSorter =
          new TreeViewerColumnValueSorter(viewer, values);

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
      duration.setLabelProvider(new TreePathDurationLabelProvider(values));

      TreeViewerColumn graph = newTreeViewerColumn(viewer, SWT.LEFT);
      graph.getColumn().setWidth(100);
      graph.getColumn().addSelectionListener(durationSorter);
      graph.setLabelProvider(TreeViewerCellPainter.observe(values,
          values, new RGB(208, 145, 60)));
    }

    @Override
    public IContributionItem[] createToolBarItems() {
      List<IContributionItem> items = new CommonToolBarBuilder()
          .enableFilterControlAction(tree, true)
          .enableTreeAction(tree.getViewer())
          .enableGroupByAction(categories)
          .enableColorByAction(values)
          .addGroupByAction(DATE)
          .addGroupByAction(WORKSPACE, DATE)
          .addColorByAction(DATE)
          .addColorByAction(WORKSPACE)
          .build();

      return items.toArray(new IContributionItem[items.size()]);
    }

    @Override
    protected TreeColumn[] getColumns() {
      return tree.getViewer().getTree().getColumns();
    }

    @Override
    protected Category[] getSelectedCategories() {
      return categories.getSelected().toArray(new Category[0]);
    }

    @Override
    protected Category getVisualCategory() {
      return (Category)values.getVisualCategory();
    }

    @Override
    protected void setSelectedCategories(List<Category> categories) {
      this.categories.setSelected(categories.toArray(
          new Category[categories.size()]));
    }

    @Override
    protected void setVisualCategory(Category category) {
      values.setVisualCategory(category);
    }

    private CategoryProvider createCategoryProvider() {
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

    private TreePathContentProvider createContentProvider() {
      TreePathContentProvider provider = new TreePathContentProvider(
          new SessionDataTreeBuilder(categories));
      provider.addObserver(new Observer() {
        @Override
        public void update(Observable o, Object arg) {
          values.setVisualCategory(values.getVisualCategory());
        }
      });
      return provider;
    }

    private TreePathValueProvider createValueProvider() {
      Map<Predicate<Object>, Category> categories = ImmutableMap.of(
          instanceOf(LocalDate.class), DATE,
          instanceOf(WorkspaceStorage.class), WORKSPACE);
      ICategorizer categorizer = new Categorizer(categories);
      IConverter<TreePath> converter = new TreePathDurationConverter();
      TreePathValueProvider provider = new TreePathValueProvider(categorizer,
          contents, converter, DATE);
      provider.addObserver(new Observer() {
        @Override
        public void update(Observable o, Object arg) {
          refresh(tree.getViewer());
        }
      });
      return provider;
    }

    private ILabelDecorator getPlatformLabelDecorator() {
      return PlatformUI.getWorkbench().getDecoratorManager()
          .getLabelDecorator();
    }

    @Override
    public String getName() {
      return "Viewer";
    }

    @Override
    public void update(final Collection<ISessionData> data) {
      Viewers.setInput(tree.getViewer(), new ISessionDataProvider() {
        @Override
        public Collection<ISessionData> get() {
          return data;
        }
      });
    }
  }

  private static final class ChartPage implements IDataPage<ISessionData> {

    private ChartComposite chartComposite;
    private SimpleVisualProvider visual;
    private Collection<ISessionData> data;

    private final TaskSeries series;
    private final Date start;
    private final Date end;

    ChartPage() {
      series = new TaskSeries("");
      start = new Date();
      end = new Date();
    }

    @Override
    public void createContents(Composite parent) {
      TaskSeriesCollection dataset = new TaskSeriesCollection();
      dataset.add(series);

      String title = null;
      String categoryAxisLabel = null;
      String dateAxisLabel = null;
      boolean showLegend = false;
      boolean showTooltips = true;
      boolean showUrls = false;
      JFreeChart chart = createGanttChart(title, categoryAxisLabel,
          dateAxisLabel, dataset, showLegend, showTooltips, showUrls);

      CategoryPlot plot = chart.getCategoryPlot();
      plot.setRangeGridlinePaint(LIGHT_GRAY);
      plot.setBackgroundPaint(WHITE);
      plot.setOutlineVisible(false);
      plot.setNoDataMessage("No data");

      IntervalBarRenderer renderer = (IntervalBarRenderer)plot.getRenderer();
      renderer.setSeriesPaint(0, new Color(208, 145, 60));
      renderer.setShadowPaint(LIGHT_GRAY);
      renderer.setBarPainter(new StandardBarPainter());

      chartComposite = new ChartComposite(parent, SWT.NONE, chart, true);
    }

    @Override
    public IContributionItem[] createToolBarItems() {
      visual = new SimpleVisualProvider();
      visual.setVisualCategory(DATE);
      visual.addObserver(new Observer() {
        @Override
        public void update(Observable o, Object arg) {
          updateChart(data);
        }
      });

      List<IContributionItem> items = new CommonToolBarBuilder()
          .enableColorByAction(visual)
          .addColorByAction(DATE)
          .addColorByAction(WORKSPACE)
          .build();

      return items.toArray(new IContributionItem[items.size()]);
    }

    private void updateChart(Collection<ISessionData> data) {
      series.removeAll();

      ICategory category = visual.getVisualCategory();
      Map<Object, Task> tasks = newHashMap();
      SimpleTimePeriod undefined = new SimpleTimePeriod(start, end);
      for (ISessionData d : data) {
        List<Interval> intervals = d.get(ISessionData.INTERVALS);
        if (intervals.isEmpty()) {
          continue;
        }

        Object key;
        String name;
        LocalDate date = d.get(ISessionData.DATE);
        if (DATE.equals(category)) {
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
          task = new Task(name, undefined);
          tasks.put(key, task);
        }
        series.add(task);
        for (Interval interval : intervals) {
          long start = interval.getStartMillis();
          long end = interval.getEndMillis();
          task.addSubtask(new Task("", new Date(start), new Date(end)));
        }
      }

      chartComposite.redraw();
    }

    @Override
    public String getName() {
      return "Chart";
    }

    @Override
    public void onRestoreState(IMemento memento) {
    }

    @Override
    public void onSaveState(IMemento memento) {
    }

    @Override
    public void update(Collection<ISessionData> data) {
      this.data = data;
      updateChart(data);
    }
  }

  private ChartPage chart;

  public SessionPage() {
    chart = new ChartPage();
  }

  @Override
  public Job updateJob(final Preference pref) {
    final LocalDate start = LocalDate.fromCalendarFields(pref.getStartDate());
    final LocalDate end = LocalDate.fromCalendarFields(pref.getEndDate());
    return new AsyncJob<ISessionData>(getAccessor(), start, end) {
      @Override
      protected void onFinishUi(Collection<ISessionData> data) {
        super.onFinishUi(data);
        chart.start.setTime(pref.getStartDate().getTimeInMillis());
        chart.end.setTime(pref.getEndDate().getTimeInMillis());
        update(data);
      }
    };
  }

  private IAccessor<ISessionData> getAccessor() {
    return DataHandler.getAccessor(ISessionData.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected IDataPage<ISessionData>[] getPages() {
    return new IDataPage[]{new ViewerPage(), chart};
  }
}
