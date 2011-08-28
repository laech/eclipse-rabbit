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
package rabbit.ui.internal;

import rabbit.tracking.internal.TrackingPlugin;
import rabbit.ui.IPage;
import rabbit.ui.Preference;
import rabbit.ui.internal.extension.CategoryDescriptor;
import rabbit.ui.internal.extension.PageDescriptor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newTreeMap;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A view to show metrics.
 */
public final class RabbitView extends ViewPart {

  private static class ImageDisposer implements DisposeListener {
    private final Image image;

    ImageDisposer(Image image) {
      this.image = checkNotNull(image);
    }

    @Override
    public void widgetDisposed(DisposeEvent e) {
      image.dispose();
    }
  }

  private static final CategoryDescriptor DEFAULT =
      new CategoryDescriptor("", "");

  /** Preference constant for saving/restoring the view state. */
  private static final String PREF_RABBIT_VIEW = "rabbitView";

  /**
   * Checks whether the two calendars has the same year, month, and day of
   * month.
   * 
   * @param date1 The calendar.
   * @param date2 The other calendar.
   * @return True if the dates has the same year, month, and day of month, false
   *         otherwise.
   */
  public static boolean isSameDate(Calendar date1, Calendar date2) {
    return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
        && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
        && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Updates the date with the data from the widget.
   * 
   * @param date The date to be updated.
   * @param widget The widget to get the data from.
   */
  public static void updateDate(Calendar date, DateTime widget) {
    date.set(Calendar.YEAR, widget.getYear());
    date.set(Calendar.MONTH, widget.getMonth());
    date.set(Calendar.DAY_OF_MONTH, widget.getDay());
  }

  /**
   * Updates the widget with the data from the date.
   * 
   * @param widget The widget to be updated.
   * @param date The date to get the data from.
   */
  public static void updateDateTime(DateTime widget, Calendar date) {
    widget.setYear(date.get(Calendar.YEAR));
    widget.setMonth(date.get(Calendar.MONTH));
    widget.setDay(date.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Gets the version of Eclipse. Not completely reliable.
   * 
   * @return The version String, such as 3.5..., or an empty String.
   */
  private static String getProductVersion() {
    try {
      IProduct product = Platform.getProduct();
      String aboutText = product.getProperty("aboutText");
      String pattern = "Version: (.*)\n";
      Pattern p = Pattern.compile(pattern);
      Matcher m = p.matcher(aboutText);
      return (m.find()) ? m.group(1) : "";
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * A map containing page status (updated or not), if a page is not updated
   * (value return false), then it will be updated before it's displayed (when a
   * user clicks on a tree node).
   */
  private Map<IPage, Boolean> pageStatus;

  /** A map containing pages and the root composite of the page. */
  private Map<IPage, Composite> pages;

  /** A map containing pages and their tool bar items. */
  private Map<IPage, IContributionItem[]> pageToolItems;

  /** A tool bar for pages to create their tool bar items. */
  private IToolBarManager extensionToolBar;

  private FormToolkit toolkit;

  /**
   * The layout of {@link #displayPanel}, used to show/hide pages on user
   * selection.
   */
  private StackLayout stackLayout;

  /** The composite to show the page that is selected by the user. */
  private Composite displayPanel;

  /** The preferences for the pages. */
  private final Preference preferences;

  /** True if this OS is Windows, false otherwise. */
  private final boolean isWindowsOS = Platform.getOS()
      .equals(Platform.OS_WIN32);

  /** True if this OS is linux, false otherwise. */
  private final boolean isLinux = Platform.getOS().equals(Platform.OS_LINUX);

  /** File to save/restore the view state, may be null. */
  private IMemento memento;

  /**
   * Constructs a new view.
   */
  public RabbitView() {
    pages = new HashMap<IPage, Composite>();
    pageStatus = new HashMap<IPage, Boolean>();
    pageToolItems = new HashMap<IPage, IContributionItem[]>();

    toolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
    stackLayout = new StackLayout();
    preferences = new Preference();
  }

  @Override
  public void createPartControl(Composite parent) {
    Form form = toolkit.createForm(parent);
    // form.getBody().setLayout(new FillLayout());

    Composite right = form.getBody();
    GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(right);

    // Header:
    Composite header = toolkit.createComposite(right);
    GridLayout headerLayout = new GridLayout(3, false);
    if (isLinux) { // Make GTK widgets have less spaces:
      headerLayout.marginHeight = 0;
      headerLayout.marginWidth = 0;
      headerLayout.horizontalSpacing = 0;
      headerLayout.verticalSpacing = 0;
    }
    header.setLayout(headerLayout);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
        .grab(true, false).applyTo(header);
    {
      int toolbarStyle = SWT.NONE;

      ToolBar bar = new ToolBar(header, toolbarStyle);
      final ToolBarManager metricsBar = new ToolBarManager(bar);
      metricsBar.add(new Action("Select Metrics", IAction.AS_PUSH_BUTTON) {
        Menu menu = buildPagesMenu(metricsBar.getControl().getShell(), this);

        @Override
        public void runWithEvent(Event event) {
          super.runWithEvent(event);
          menu.setLocation(metricsBar.getControl().toDisplay(event.x, event.y));
          menu.setVisible(true);
        }
      });
      metricsBar.update(false);
      GridDataFactory
          .swtDefaults()
          .hint(120, SWT.DEFAULT)
          .align(SWT.BEGINNING, SWT.FILL)
          .applyTo(bar);

      bar = new ToolBar(header, toolbarStyle);
      bar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      extensionToolBar = new ToolBarManager(bar);

      bar = new ToolBar(header, toolbarStyle);
      createToolBarItems(new ToolBarManager(bar));
    }
    displayPanel = toolkit.createComposite(right);
    displayPanel.setLayout(stackLayout);
    GridDataFactory.fillDefaults().grab(true, true).span(3, 1)
        .applyTo(displayPanel);

    // Greeting message:
    Composite cmp = toolkit.createComposite(displayPanel);
    cmp.setLayout(new GridLayout());
    {
      Label imgLabel = toolkit.createLabel(cmp, "", SWT.CENTER);
      imgLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
      imgLabel.setImage(getTitleImage());

      Label helloLabel = toolkit.createLabel(cmp, "Welcome!", SWT.CENTER);
      helloLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
    }
    stackLayout.topControl = cmp;
    displayPanel.layout();

    if (memento != null) {
      restoreState(memento);
    }
  }

  /**
   * Displays the given page.
   * 
   * @param page The page to display.
   */
  public void display(IPage page) {
    // Removes the extension tool bar items:
    for (IContributionItem item : extensionToolBar.getItems()) {
      item.setVisible(false);
    }

    Composite cmp = null;
    if (page != null) {

      // Updates the page:
      cmp = pages.get(page);
      if (cmp == null) {
        cmp = toolkit.createComposite(displayPanel);
        cmp.setLayout(new FillLayout());
        page.createContents(cmp);
        pages.put(page, cmp);

        // Restores the state:
        if (memento != null) {
          page.onRestoreState(memento);
        }
      }

      // Updates the extension tool bar items:
      IContributionItem[] items = pageToolItems.get(page);
      if (items == null) {
        items = page.createToolBarItems(extensionToolBar);
        pageToolItems.put(page, items);
      } else {
        for (IContributionItem item : items) {
          item.setVisible(true);
        }
      }

      // Updates the current visible page, mark others as not updated:
      Boolean updated = pageStatus.get(page);
      if (updated == null || !updated) {
        pageStatus.put(page, Boolean.TRUE);
        updatePage(page, preferences);
      }
    }

    extensionToolBar.update(true);
    stackLayout.topControl = cmp;
    displayPanel.layout();
  }

  @Override
  public void dispose() {
    toolkit.dispose();
    super.dispose();
  }

  @Override
  public void init(IViewSite site, IMemento m) throws PartInitException {
    super.init(site, m);
    if (m != null) {
      this.memento = m.getChild(PREF_RABBIT_VIEW);
    }
  }

  @Override
  public void saveState(IMemento memento) {
    memento = memento.createChild(PREF_RABBIT_VIEW);
    for (IPage page : pages.keySet()) {
      page.onSaveState(memento);
    }
  }

  @Override
  public void setFocus() {
  }

  private void createSpace(IToolBarManager toolBar) {
    createString(toolBar, "  ");
  }

  private void createString(IToolBarManager toolBar, final String str) {
    toolBar.add(new ControlContribution(null) {
      @Override
      protected Control createControl(Composite parent) {
        return toolkit.createLabel(parent, str);
      }
    });
  }

  /**
   * Creates tool bar items for non windows operating systems.
   * 
   * @param toolBar The tool bar.
   */
  private void createToolBarForNonWindowsOS(IToolBarManager toolBar) {
    CalendarAction.create(toolBar, getSite().getShell(), preferences
        .getStartDate(), " From: ", " ");
    CalendarAction.create(toolBar, getSite().getShell(), preferences
        .getEndDate(), " To: ", " ");
  }

  /**
   * Creates tool bar items for Windows operating system.
   * 
   * @param toolBar The tool bar.
   */
  private void createToolBarForWindowsOS(IToolBarManager toolBar) {
    toolBar.add(new ControlContribution("rabbit.ui.fromDateTime") {
      @Override
      protected Control createControl(Composite parent) {
        final Calendar dateToBind = preferences.getStartDate();
        final DateTime fromDateTime = new DateTime(parent, SWT.DROP_DOWN
            | SWT.BORDER);
        fromDateTime
            .setToolTipText("Select the start date for the data to be displayed");
        updateDateTime(fromDateTime, dateToBind);
        fromDateTime.addListener(SWT.Selection, new Listener() {
          @Override
          public void handleEvent(Event event) {
            updateDate(dateToBind, fromDateTime);
          }
        });
        return fromDateTime;
      }
    });
    createSpace(toolBar);
    toolBar.add(new ControlContribution("rabbit.ui.toDateTime") {
      @Override
      protected Control createControl(Composite parent) {
        final Calendar dateToBind = preferences.getEndDate();
        final DateTime toDateTime = new DateTime(parent, SWT.DROP_DOWN
            | SWT.BORDER);
        toDateTime
            .setToolTipText("Select the end date for the data to be displayed");
        updateDateTime(toDateTime, dateToBind);
        toDateTime.addListener(SWT.Selection, new Listener() {
          @Override
          public void handleEvent(Event event) {
            updateDate(dateToBind, toDateTime);
          }
        });
        return toDateTime;
      }
    });
  }

  /**
   * Creates the tool bar items.
   * 
   * @param toolBar The tool bar.
   */
  private void createToolBarItems(IToolBarManager toolBar) {
    // Only Windows && Eclipse 3.5 has SWT.DROP_DOWN for DateTime.
    // We don't support 3.3 and before anyway:
    boolean isDropDownDateTimeSupported = !getProductVersion()
        .startsWith("3.4");

    if (isWindowsOS && isDropDownDateTimeSupported) {
      createToolBarForWindowsOS(toolBar);
    } else {
      createToolBarForNonWindowsOS(toolBar);
    }

    if (isWindowsOS) { // Looks better:
      createSpace(toolBar);
    }

    IAction refresh = new Action("Refresh") {
      @Override
      public void run() {
        updateView();
      }
    };

    /*
     * Mainly for Eclipse 3.4 (no SWT.DROP_DOWN DateTime) on Windows. Things
     * look ugly on Windows if some tool bar actions have text and some have
     * icons, so in this case, no icons at all.
     */
    if (!isWindowsOS || isDropDownDateTimeSupported) {
      refresh.setImageDescriptor(SharedImages.REFRESH);
    }

    //
    toolBar.add(refresh);
    toolBar.update(true);
  }

  /**
   * Restores the view state.
   * 
   * @param memento The settings.
   */
  private void restoreState(IMemento memento) {
    memento = memento.getChild(PREF_RABBIT_VIEW);
    if (memento == null) {
      return;
    }
  }

  private void updatePage(IPage page, Preference preference) {
    final Job job = page.updateJob(preference);
    if (job == null) {
      return;
    }

    final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService)
        getSite().getService(IWorkbenchSiteProgressService.class);

    if (service != null) {
      service.schedule(job);
    }
  }

  private SortedMap<CategoryDescriptor, List<PageDescriptor>> loadPages() {
    final RabbitUI ui = RabbitUI.getDefault();

    final SortedMap<CategoryDescriptor, List<PageDescriptor>> tree = newTreeMap();
    tree.put(DEFAULT, Lists.<PageDescriptor> newLinkedList());
    for (CategoryDescriptor category : ui.getPageCategories()) {
      tree.put(category, Lists.<PageDescriptor> newLinkedList());
    }

    for (PageDescriptor page : ui.getPages()) {
      final String categoryId = page.getCategoryId();
      boolean foundCategory = false;
      for (Entry<CategoryDescriptor, List<PageDescriptor>> e : tree.entrySet()) {
        if (Objects.equal(categoryId, e.getKey().getId())) {
          e.getValue().add(page);
          foundCategory = true;
          break;
        }
      }
      if (!foundCategory) {
        tree.get(DEFAULT).add(page);
      }
    }

    return tree;
  }

  private Menu buildPagesMenu(Decorations menuParent, final IAction menuTaget) {
    final SortedMap<CategoryDescriptor, List<PageDescriptor>> tree = loadPages();

    final Menu menu = new Menu(menuParent, SWT.POP_UP);
    final SelectionListener itemSelectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        super.widgetSelected(e);
        final MenuItem item = (MenuItem)e.widget;
        menuTaget.setText(item.getText());
        display((IPage)item.getData());
      }
    };

    for (Entry<CategoryDescriptor, List<PageDescriptor>> e : tree.entrySet()) {
      final CategoryDescriptor category = e.getKey();
      final List<PageDescriptor> pages = e.getValue();
      if (pages.isEmpty()) {
        continue;
      }
      if (menu.getItemCount() > 0) {
        new MenuItem(menu, SWT.SEPARATOR);
      }
      if (!DEFAULT.equals(category)) {
        final MenuItem item = new MenuItem(menu, SWT.NONE);
        item.setEnabled(false);
        item.setText(category.getName());
      }

      Collections.sort(pages);
      for (PageDescriptor page : pages) {
        final MenuItem item = new MenuItem(menu, SWT.NONE);
        item.setText(page.getName());
        item.setData(page.getPage());
        item.addSelectionListener(itemSelectionListener);
        final ImageDescriptor icon = page.getIcon();
        final Image image = (icon == null) ? null : icon.createImage();
        if (image != null) {
          item.setImage(image);
          item.addDisposeListener(new ImageDisposer(image));
        }
      }
    }
    return menu;
  }

  /**
   * Updates the pages to current preference.
   */
  private void updateView() {
    // Sync with today's data:
    Calendar today = Calendar.getInstance();
    if (isSameDate(today, preferences.getEndDate())
        || today.before(preferences.getEndDate())) {
      TrackingPlugin.getDefault().saveCurrentData();
    }

    // Mark all invisible pages as "not yet updated":
    for (Map.Entry<IPage, Composite> entry : pages.entrySet()) {
      boolean isVisible = stackLayout.topControl == entry.getValue();
      if (isVisible) {
        // update current visible page.
        updatePage(entry.getKey(), preferences);
      }
      pageStatus.put(entry.getKey(), Boolean.valueOf(isVisible));
    }
  }
}
