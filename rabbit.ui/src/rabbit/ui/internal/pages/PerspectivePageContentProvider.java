package rabbit.ui.internal.pages;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.AbstractTreeContentProvider;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static com.google.common.base.Preconditions.checkNotNull;

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
 * Collection<PerspectiveDataDescriptor>}.
 */
public class PerspectivePageContentProvider extends AbstractTreeContentProvider {

  /** Function to categories the data by dates. */
  @Nonnull
  private Function<PerspectiveDataDescriptor, LocalDate> categorizeByDateFunction;

  /** Categorized mapping from dates to descriptors. */
  @Nonnull
  private ImmutableMultimap<LocalDate, PerspectiveDataDescriptor> dateToPerspectives;

  private boolean displayByDate;

  @Nonnull
  private PerspectivePage page;

  /** Maps from perspective ID to IPerspectiveDescriptor. */
  @Nonnull
  private final BiMap<String, IPerspectiveDescriptor> perspectives;

  /** Summary mapping from perspective to total duration. */
  @Nonnull
  private ImmutableMap<IPerspectiveDescriptor, Long> perspectveSummaries;

  public PerspectivePageContentProvider(PerspectivePage page) {
    checkNotNull(page);
    this.page = page;
    displayByDate = true;
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

  /**
   * Checks whether the data is categorized by dates.
   * 
   * @return True if the data is categorized by dates, false otherwise.
   */
  public boolean isDisplayingByDate() {
    return displayByDate;
  }

  /**
   * Sets whether the data is categorized by dates.
   * 
   * @param displayByDate True to set to display by dates, false otherwise.
   */
  public void setDisplayByDate(boolean displayByDate) {
    if (isDisplayingByDate() == displayByDate)
      return;
    this.displayByDate = displayByDate;
    updatePageMaxValue();

    page.getViewer().getTree().setRedraw(false);
    page.getViewer().refresh(true);
    page.getViewer().getTree().setRedraw(true);
  }

  private void updatePageMaxValue() {
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
