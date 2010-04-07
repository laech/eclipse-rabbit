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
package rabbit.ui.internal.pages;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Content provider for a {@link PerspectivePage}. Accepts input as {@code
 * Iterable<PerspectiveDataDescriptor>}.
 */
public class PerspectivePageContentProvider extends
    AbstractDateCategoryContentProvider {

  /** Function to categories the data by dates. */
  @Nonnull
  private Function<PerspectiveDataDescriptor, LocalDate> categorizeByDateFunction;

  /** Categorized mapping from dates to descriptors. */
  @Nonnull
  private ImmutableMultimap<LocalDate, PerspectiveDataDescriptor> dateToPerspectives;

  /** Maps from perspective ID to IPerspectiveDescriptor. */
  @Nonnull
  private final BiMap<String, IPerspectiveDescriptor> perspectives;

  /** Summary mapping from perspective to total duration. */
  @Nonnull
  private ImmutableMap<IPerspectiveDescriptor, Long> perspectveSummaries;

  /**
   * Constructor.
   * 
   * @param page The parent page.
   * @param displayByDate True to display data by date.
   */
  public PerspectivePageContentProvider(@Nonnull PerspectivePage page,
      boolean displayByDate) {
    super(page, displayByDate);
    perspectveSummaries = ImmutableMap.of();
    dateToPerspectives = ImmutableMultimap.of();

    perspectives = HashBiMap.create();
    for (IPerspectiveDescriptor des : PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()) {
      perspectives.put(des.getId(), des);
    }

    categorizeByDateFunction = new Function<PerspectiveDataDescriptor, LocalDate>() {
      @Override
      public LocalDate apply(PerspectiveDataDescriptor from) {
        return from.getDate();
      }
    };
  }

  @Override
  public Object[] getChildren(Object element) {
    if (element instanceof LocalDate) {
      Collection<PerspectiveDataDescriptor> des = dateToPerspectives
          .get((LocalDate) element);
      if (des != null) {
        return des.toArray(new Object[des.size()]);
      }
    }
    return EMPTY_ARRAY;
  }

  @Override
  public Object[] getElements(Object inputElement) {
    if (isDisplayingByDate()) {
      Set<LocalDate> dates = dateToPerspectives.keySet();
      return dates.toArray(new Object[dates.size()]);
    } else {
      return perspectveSummaries.keySet().toArray(
          new Object[perspectveSummaries.size()]);
    }
  }

  /**
   * Gets the perspective from the given argument.
   * 
   * @param des The descriptor containing the perspective ID.
   * @return A perspective descriptor, never null. If the perspective ID does
   *         not exit in the workbench perspective registry, a custom descriptor
   *         is returned.
   */
  public IPerspectiveDescriptor getPerspective(PerspectiveDataDescriptor des) {
    IPerspectiveDescriptor pd = perspectives.get(des.getPerspectiveId());
    if (pd == null) {
      pd = new UndefinedPerspectiveDescriptor(des.getPerspectiveId());
      perspectives.put(des.getPerspectiveId(), pd);
    }
    return pd;
  }

  /**
   * Gets the value (duration in milliseconds) of the perspective.
   * 
   * @param des The perspective.
   * @return Duration in milliseconds.
   */
  public long getValueOfPerspective(IPerspectiveDescriptor des) {
    Long value = perspectveSummaries.get(des);
    return (value == null) ? 0 : value;
  }

  @Override
  public boolean hasChildren(Object element) {
    // Only a date can have children elements:
    return dateToPerspectives.containsKey(element);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    if (newInput == null) {
      dateToPerspectives = ImmutableMultimap.of();
      perspectveSummaries = ImmutableMap.of();
      return;
    }
    Iterable<PerspectiveDataDescriptor> data = (Iterable<PerspectiveDataDescriptor>) newInput;

    // Updates perspective summary:
    Map<IPerspectiveDescriptor, Long> perspToDuration = Maps.newLinkedHashMap();
    for (PerspectiveDataDescriptor des : data) {
      IPerspectiveDescriptor perspective = getPerspective(des);
      Long value = perspToDuration.get(perspective);
      perspToDuration.put(perspective, //
          (value != null) ? des.getValue() + value : des.getValue());
    }
    perspectveSummaries = ImmutableMap.copyOf(perspToDuration);
    dateToPerspectives = Multimaps.index(data, categorizeByDateFunction);

    updatePageMaxValue();
  }

  @Override
  protected void updatePageMaxValue() {
    long maxValue = 0;
    if (isDisplayingByDate()) {
      for (PerspectiveDataDescriptor des : dateToPerspectives.values()) {
        if (des.getValue() > maxValue)
          maxValue = des.getValue();
      }
    } else {
      for (long value : perspectveSummaries.values()) {
        if (value > maxValue)
          maxValue = value;
      }
    }
    page.setMaxValue(maxValue);
  }
}
