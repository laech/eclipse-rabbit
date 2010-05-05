package rabbit.ui.internal.pages;

import static rabbit.ui.internal.util.DurationFormat.format;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;

// TODO
@SuppressWarnings("restriction")
public class JavaPageLabelProvider extends LabelProvider
    implements ITableLabelProvider, IColorProvider {
  
  private final DateLabelProvider dateLabels;
  private final JavaElementLabelProvider javaLabels;
  private final JavaPageContentProvider contentProvider;
  private final Color gray;

  public JavaPageLabelProvider(JavaPageContentProvider contentProvider) {
    checkNotNull(contentProvider);
    dateLabels = new DateLabelProvider();
    javaLabels = new JavaElementLabelProvider(
        JavaElementLabelProvider.SHOW_DEFAULT | 
        JavaElementLabelProvider.SHOW_RETURN_TYPE |
        JavaElementLabelProvider.SHOW_SMALL_ICONS);
    this.contentProvider = contentProvider;
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
  }
  
  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
    javaLabels.dispose();
  }
  
  @Override
  public String getText(Object element) {
    if (element instanceof TreeNode) {
      element = ((TreeNode) element).getValue();
    }
    if (element instanceof LocalDate) {
      return dateLabels.getText(element);
    }
    return javaLabels.getText(element);
  }
  
  @Override
  public Image getImage(Object element) {
    if (element instanceof TreeNode) {
      element = ((TreeNode) element).getValue();
    }
    if (element instanceof LocalDate) {
      return dateLabels.getImage(element);
    }
    return javaLabels.getImage(element);
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    return (columnIndex == 0) ? getImage(element) : null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    switch (columnIndex) {
    case 0:
      return getText(element);
    case 1:
      if (contentProvider.shouldPaint(element)) {
        return format(contentProvider.getValue(element));
      }
      return null;
    default:
      return null;
    }
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof TreeNode) {
      element = ((TreeNode) element).getValue();
    }
    if (element instanceof IJavaElement) {
      return ((IJavaElement) element).exists() ? null : gray;
    }
    return null;
  }

  
}
