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

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Content provider for a {@link PartPage}. Acceptable input is {@code
 * Iterable<PartDataDescriptor>}.
 */
public class PartPageContentProvider extends
    AbstractDateCategoryContentProvider {

  /** Map of part to total duration. */
  @Nonnull
  private ImmutableMap<IWorkbenchPartDescriptor, Long> partSummaries;
  /** Map of date to part data. */
  @Nonnull
  private ImmutableMultimap<LocalDate, PartDataDescriptor> dateToParts;
  /** All editors and view extensions. */
  @Nonnull
  private final Map<String, IWorkbenchPartDescriptor> parts;
  /** Function to categorize the data by date. */
  @Nonnull
  private Function<PartDataDescriptor, LocalDate> categorizeByDateFunction;

  public PartPageContentProvider(PartPage page, boolean displayByDate) {
    super(page, displayByDate);
    resetData();

    // Loads all the editors and views:
    parts = Maps.newLinkedHashMap();
    for (IFileEditorMapping mapping : PlatformUI.getWorkbench()
        .getEditorRegistry().getFileEditorMappings()) {
      for (IEditorDescriptor editor : mapping.getEditors())
        parts.put(editor.getId(), editor);
    }
    for (IViewDescriptor des : PlatformUI.getWorkbench().getViewRegistry()
        .getViews()) {
      parts.put(des.getId(), des);
    }

    // The function:
    categorizeByDateFunction = new Function<PartDataDescriptor, LocalDate>() {
      @Override
      public LocalDate apply(PartDataDescriptor from) {
        return from.getDate();
      }
    };
  }

  /** Resets all data field to empty. */
  private void resetData() {
    partSummaries = ImmutableMap.of();
    dateToParts = ImmutableMultimap.of();
  }

  @Override
  public Object[] getChildren(Object element) {
    if (element instanceof LocalDate) {
      Collection<?> parts = dateToParts.get((LocalDate) element);
      return parts.toArray(new Object[parts.size()]);
    }
    return EMPTY_ARRAY;
  }

  @Override
  public boolean hasChildren(Object element) {
    return dateToParts.containsKey(element);
  }

  @Override
  public Object[] getElements(Object input) {
    if (isDisplayingByDate()) {
      Set<LocalDate> dates = dateToParts.keySet();
      return dates.toArray(new Object[dates.size()]);
    } else {
      return partSummaries.keySet().toArray(new Object[partSummaries.size()]);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    if (newInput == null) {
      resetData();
      return;
    }

    Iterable<PartDataDescriptor> data = (Iterable<PartDataDescriptor>) newInput;
    dateToParts = Multimaps.index(data, categorizeByDateFunction);

    Map<IWorkbenchPartDescriptor, Long> sums = Maps.newLinkedHashMap();
    for (PartDataDescriptor des : data) {
      IWorkbenchPartDescriptor part = getPart(des);
      Long value = sums.get(part);
      sums.put(part, (value == null) ? des.getValue() : des.getValue() + value);
    }
    partSummaries = ImmutableMap.copyOf(sums);
    
    updatePageMaxValue();
  }

  @Override
  protected void updatePageMaxValue() {
    long max = 0;
    if (isDisplayingByDate()) {
      for (PartDataDescriptor des : dateToParts.values()) {
        if (des.getValue() > max)
          max = des.getValue();
      }
    } else {
      for (Long value : partSummaries.values()) {
        if (value > max)
          max = value;
      }
    }
    page.setMaxValue(max);
  }

  /**
   * Gets the workbench part with the ID from the data descriptor.
   * 
   * @param des The data descriptor containing the ID.
   * @return A workbench part descriptor, never null. If no parts contain the
   *         ID, a custom part descriptor is returned.
   */
  @Nonnull
  public IWorkbenchPartDescriptor getPart(PartDataDescriptor des) {
    IWorkbenchPartDescriptor part = parts.get(des.getPartId());
    if (part == null) {
      part = new UndefinedWorkbenchPartDescriptor(des.getPartId());
      parts.put(part.getId(), part);
    }
    return part;
  }

  /**
   * Gets the value of the workbench part.
   * 
   * @param part The workbench part.
   * @return The value.
   */
  public long getValueOfPart(IWorkbenchPartDescriptor part) {
    Long value = partSummaries.get(part);
    return (value == null) ? 0 : value;
  }
}
