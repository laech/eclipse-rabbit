package rabbit.tracking.internal.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;

// TODO test
public final class WorkbenchUtil {

  public static IWorkbenchWindow getActiveWindow() {
    if (Display.getCurrent() != null) {
      return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    final IWorkbenchWindow[] win = new IWorkbenchWindow[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        win[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      }
    });
    return win[0];
  }

  public static boolean isActiveShell(IWorkbenchWindow win) {
    final Shell shell = win.getShell();
    final boolean[] result = new boolean[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        result[0] = shell.getDisplay().getActiveShell() == shell
            && !shell.getMinimized();
      }
    });
    return result[0];
  }

  public static IWorkbenchPart getActivePart() {
    return getActiveWindow().getPartService().getActivePart();
  }

  /**
   * Gets all the {@link IPartService} from the currently opened windows.
   * 
   * @return A Set of IPartService.
   */
  public static Set<IPartService> getPartServices() {
    Set<IPartService> result = new LinkedHashSet<IPartService>();
    IWorkbenchWindow[] ws = PlatformUI.getWorkbench().getWorkbenchWindows();
    for (IWorkbenchWindow w : ws) {
      result.add(w.getPartService());
    }
    return result;
  }

  /**
   * Gets the perspective of the given window.
   * 
   * @param win The window.
   * @return The perspective, or null.
   */
  public static IPerspectiveDescriptor getPerspective(
      @Nullable IWorkbenchWindow win) {
    if (win == null) {
      return null;
    }
    IWorkbenchPage page = win.getActivePage();
    if (page != null) {
      return page.getPerspective();
    }
    return null;
  }
}
