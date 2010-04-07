package rabbit.ui.internal.pages;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.PerspectiveLabelProvider;
import org.joda.time.LocalDate;

/**
 * Label provider for providing labels for a {@link PerspectivePage}'s name
 * column.
 */
public class PerspectivePageNameLabelProvider extends StyledCellLabelProvider {

  private LocalDate today;
  private final Image calendarImg;
  private final Color undefinedColor;
  private final PerspectiveLabelProvider provider;
  private final PerspectivePageContentProvider content;

  /**
   * Constructor.
   * 
   * @param content The content provider for the page.
   */
  public PerspectivePageNameLabelProvider(PerspectivePageContentProvider content) {
    checkNotNull(content);

    today = new LocalDate();
    calendarImg = SharedImages.CALENDAR.createImage();
    undefinedColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);

    provider = new PerspectiveLabelProvider(false);
    this.content = content;
  }

  @Override
  public void dispose() {
    calendarImg.dispose();
    provider.dispose();
    super.dispose();
  }

  @Override
  public void update(ViewerCell cell) {
    super.update(cell);
    cell.setText(null);
    cell.setStyleRanges(new StyleRange[0]);
    cell.setImage(null);
    cell.setForeground(null);

    Object element = cell.getElement();

    if (element instanceof LocalDate) {
      LocalDate date = (LocalDate) element;

      StyledString styledString = new StyledString(date.toString());
      if (date.isEqual(today))
        styledString.append(" [Today]", StyledString.DECORATIONS_STYLER);
      else if (date.getYear() == today.getYear()
          && date.getDayOfYear() == today.getDayOfYear() - 1)
        styledString.append(" [Yestoday]", StyledString.DECORATIONS_STYLER);

      cell.setText(styledString.getString());
      cell.setStyleRanges(styledString.getStyleRanges());
      cell.setImage(calendarImg);

    } else if (element instanceof IPerspectiveDescriptor) {
      cell.setText(provider.getText(element));
      cell.setImage(provider.getImage(element));

    } else if (element instanceof PerspectiveDataDescriptor) {
      IPerspectiveDescriptor des = content
          .getPerspective((PerspectiveDataDescriptor) element);
      cell.setText(provider.getText(des));
      cell.setImage(provider.getImage(des));
    }

    if (element instanceof UndefinedPerspectiveDescriptor)
      cell.setForeground(undefinedColor);
  }

  /**
   * Updates the state of this label provider, this method should be called when
   * the input of the viewer is changed.
   */
  public void updateState() {
    today = new LocalDate();
  }
}
