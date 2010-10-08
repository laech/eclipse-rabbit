package rabbit.ui.internal.pages;

import rabbit.data.access.IAccessor;
import rabbit.ui.Preference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.joda.time.LocalDate;

/**
 * Abstract class defines common behaviors for pages which use an
 * {@link IAccessor} to get the data.
 * @deprecated To be removed.
 */
@Deprecated
public abstract class AbstractAccessorPage extends AbstractFilteredTreePage {

  /**
   * Creates a new job to update the viewer.
   * 
   * @param viewer The viewer.
   * @param preference The new preferences.
   * @param accessor The accessor to get the data.
   * @param jobPriority The priority, one of the priority constants defined in
   *          {@link Job}.
   * @return A job object, not scheduled, not yet been ran.
   */
  static Job newUpdateJob(final TreeViewer viewer,
                          final Preference preference, 
                          final IAccessor<?> accessor,
                          final int jobPriority) {
    
    Job updateJob = new Job("Updating Rabbit View...") {
      TreePath[] expandedPaths = null;
      Object input = null;

      @Override
      protected IStatus run(IProgressMonitor monitor) {
        if (monitor.isCanceled())
          return Status.CANCEL_STATUS;
        else
          monitor.beginTask("Updating page...", 2);

        LocalDate start = LocalDate.fromCalendarFields(preference.getStartDate());
        LocalDate end = LocalDate.fromCalendarFields(preference.getEndDate());
        input = accessor.getData(start, end);
        monitor.worked(1);

        if (monitor.isCanceled())
          return Status.CANCEL_STATUS;

        viewer.getTree().getDisplay().syncExec(new Runnable() {
          @Override
          public void run() {
            viewer.getTree().setRedraw(false);
            expandedPaths = viewer.getExpandedTreePaths();
            viewer.setInput(input);
            viewer.setExpandedTreePaths(expandedPaths);
            viewer.getTree().setRedraw(true);
          }
        });
        monitor.worked(1);

        monitor.done();
        return Status.OK_STATUS;
      }
    };
    updateJob.setPriority(jobPriority);
    return updateJob;
  }
  
  @Override
  public Job updateJob(final Preference p) {
    return newUpdateJob(getViewer(), p, getAccessor(), Job.SHORT);
  }
  
  /**
   * Gets the accessor of this page.
   * @return An accessor.
   */
  protected abstract IAccessor<?> getAccessor();
}
