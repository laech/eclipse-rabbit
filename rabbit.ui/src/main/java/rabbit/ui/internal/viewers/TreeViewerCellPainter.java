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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;

import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * A label provider for a tree viewer column that paints horizontal bars in the
 * cells.
 * <p/>
 * This class also supports caching of the values returned by an
 * {@link IValueProvider}, when enabled, it may have performance improvements on
 * large dataset, but {@link #clearCache()} must be called when the owner
 * viewer's content or structure is changed so that old values will not be used.
 * <p/>
 * This class also implements {@link Observer} who's
 * {@link #update(Observable, Object)} method will call {@link #clearCache()}.
 */
public final class TreeViewerCellPainter extends StyledCellLabelProvider
    implements Observer {

  private static final RGB DEFAULT_BACKGROUND_RGB = new RGB(84, 141, 212);

  /**
   * Creates a cache enabled {@link TreeViewerCellPainter} to observe on an
   * {@link Observable}.
   * 
   * @param provider the provider that will provide values for paths, it must
   *        support {@link IValueProvider#getValue(Object)} with the argument as
   *        a {@link List} representing a tree path
   * @param observable the object to observe on
   * @return a new {@link TreeViewerCellPainter}
   * @throws NullPointerException if {@code provider} is {@code null}
   */
  public static TreeViewerCellPainter observe(IValueProvider provider,
      Observable observable) {
    TreeViewerCellPainter painter = new TreeViewerCellPainter(provider, true);
    observable.addObserver(painter);
    return painter;
  }

  /**
   * Creates a cache enabled {@link TreeViewerCellPainter} to observe on an
   * {@link Observable}.
   * 
   * @param provider the provider that will provide values for paths, it must
   *        support {@link IValueProvider#getValue(Object)} with the argument as
   *        a {@link List} representing a tree path
   * @param observable the object to observe on
   * @param customColor optional custom color for painting
   * @return a new {@link TreeViewerCellPainter}
   * @throws NullPointerException if {@code provider} is {@code null}
   */
  public static TreeViewerCellPainter observe(IValueProvider provider,
      Observable observable, RGB customColor) {
    TreeViewerCellPainter painter = new TreeViewerCellPainter(provider, true,
        customColor);
    observable.addObserver(painter);
    return painter;
  }

  private Color customBackground;
  private Color systemForeground;
  private final RGB backgroundRgb;
  private final IValueProvider valueProvider;
  private final boolean isLinux;
  private final boolean useCache;

  /** Cache of paths and their values */
  private final Map<List<Object>, Long> cache;

  /** Used by {@link #getTreePath(TreeItem)} for storing path segments */
  private final List<Object> tmpPathHelper = new ArrayList<Object>();

  /**
   * @param provider the provider for getting the values of each tree path.
   * @throws NullPointerException if argument is null.
   * @deprecated use {@link #TreeViewerCellPainter(IValueProvider, boolean)}
   *             instead
   */
  @Deprecated
  public TreeViewerCellPainter(IValueProvider provider) {
    this(provider, false);
  }

  /**
   * Constructs a new cell painter.
   * 
   * @param provider the value provider to provide values for each path that
   *        needs to be painted, it must support
   *        {@link IValueProvider#getValue(Object)} with the argument as a
   *        {@link List} representing a tree path
   * 
   * @param useCache {@code true} to enable caching for the values returned by
   *        {@code valueProvider}, {@code false } to disable it. If enabled, you
   *        need to call {@link #clearCache()} whenever your viewer's content or
   *        structure changes.
   * 
   * @throws NullPointerException if {@code provider} is {@code null}
   */
  public TreeViewerCellPainter(IValueProvider provider, boolean useCache) {
    this(provider, useCache, null);
  }

  /**
   * Constructs a new cell painter.
   * 
   * @param provider the value provider to provide values for each path that
   *        needs to be painted, it must support
   *        {@link IValueProvider#getValue(Object)} with the argument as a
   *        {@link List} representing a tree path
   * 
   * @param useCache {@code true} to enable caching for the values returned by
   *        {@code valueProvider}, {@code false } to disable it. If enabled, you
   *        need to call {@link #clearCache()} whenever your viewer's content or
   *        structure changes.
   * 
   * @param customColor optional custom color for painting
   * 
   * @throws NullPointerException if {@code provider} is {@code null}
   */
  public TreeViewerCellPainter(
      IValueProvider provider, boolean useCache, RGB customRgbColor) {
    this.valueProvider = checkNotNull(provider, "valueProvider");
    this.isLinux = Platform.getOS().equals(Platform.OS_LINUX);
    this.useCache = useCache;
    if (useCache) {
      cache = newHashMap();
    } else {
      cache = emptyMap();
    }
    this.backgroundRgb = customRgbColor != null ? customRgbColor
        : DEFAULT_BACKGROUND_RGB;
  }

  @Override
  public void dispose() {
    customBackground.dispose();
    super.dispose();
  }

  /**
   * Gets the value provider of this painter.
   * 
   * @return The value provider.
   */
  public IValueProvider getValueProvider() {
    return valueProvider;
  }

  @Override
  public void initialize(ColumnViewer viewer, ViewerColumn column) {
    super.initialize(viewer, column);
    Display display = viewer.getControl().getDisplay();
    systemForeground = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    customBackground = createColor(display);
  }

  @Override
  public void paint(Event event, Object element) {
    if (!valueProvider.shouldPaint(element)) {
      return;
    }

    List<Object> path = getTreePath((TreeItem)event.item);
    int columnWidth = event.gc.getClipping().width;
    int width = getWidth(columnWidth, path);
    if (width == 0) {
      return;
    }
    int x = event.x;
    int y = event.y;
    int height = event.height - 1;

    GC gc = event.gc;
    Color oldBackground = gc.getBackground();
    Color oldForeground = gc.getForeground();
    int oldAnti = gc.getAntialias();
    int oldAlpha = gc.getAlpha();

    /*
     * On Linux, enabling GC's antialias or changing the alpha will sometimes
     * cause the color bar to be half drawn.
     */
    if (!isLinux) {
      int alpha = (int)(width / (float)columnWidth * 255);
      if (alpha < 100) {
        alpha = 100;
      }
      gc.setAlpha(alpha);
      gc.setAntialias(SWT.ON);
    }
    gc.setBackground(customBackground);
    gc.fillRectangle(x, y, 2, height);
    gc.fillRoundRectangle(x, y, width, height, 4, 4);

    gc.setAlpha(oldAlpha);
    gc.setAntialias(oldAnti);

    gc.setForeground(systemForeground);
    gc.drawLine(x, y, x, y + height - 1);
    gc.drawLine(x + width, y, x + width, y + height - 1);

    gc.setBackground(oldBackground);
    gc.setForeground(oldForeground);
  }

  /**
   * Clears the value cache. Has no affect is cache is not enabled.
   * 
   * @see #TreeViewerCellPainter(IValueProvider, boolean)
   */
  public void clearCache() {
    if (useCache) {
      cache.clear();
    }
  }

  private Color createColor(Display display) {
    return new Color(display, backgroundRgb);
  }

  private List<Object> getTreePath(TreeItem item) {
    tmpPathHelper.clear();
    while (item != null) {
      tmpPathHelper.add(0, item.getData());
      item = item.getParentItem();
    }
    return tmpPathHelper;
  }

  /**
   * Gets the width in pixels for the paint.
   */
  private int getWidth(int columnWidth, List<Object> path) {
    long maxValue = valueProvider.getMaxValue();
    if (maxValue == 0) {
      return 0;
    }

    long value = 0;
    if (useCache) {
      Long cached = cache.get(path);
      if (cached == null) {
        cached = valueProvider.getValue(path);
        cache.put(newArrayList(path), cached);
      }
      value = cached.longValue();
    } else {
      value = valueProvider.getValue(path);
    }
    int width = (int)(value * columnWidth / (double)maxValue);
    width = ((value != 0) && (width == 0)) ? 2 : width;

    if (value != 0 && width < 2) {
      width = 2;
    }
    return width;
  }

  @Override
  public void update(Observable o, Object arg) {
    clearCache();
  }
}