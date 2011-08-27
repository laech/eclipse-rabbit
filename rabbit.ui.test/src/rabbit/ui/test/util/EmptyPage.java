package rabbit.ui.test.util;

import rabbit.ui.IPage;
import rabbit.ui.Preference;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * A page that does nothing.
 */
public class EmptyPage implements IPage {

  @Override
  public void createContents(Composite parent) {
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    return new IContributionItem[0];
  }

  @Override
  public void onRestoreState(IMemento memento) {
  }

  @Override
  public void onSaveState(IMemento memento) {
  }

  @Override
  public Job updateJob(Preference preference) {
    return null;
  }
}
