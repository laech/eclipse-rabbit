/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.tracking.workbench;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import rabbit.tracking.AbstractTracker;
import rabbit.tracking.IUserListener;
import rabbit.tracking.IUserMonitorService;

/**
 * A tracker listening on an {@link IUserMonitorService} and notifies subclass
 * when user event happens. A subclass will need to implement
 * {@link #onUserActive()} and {@link #onUserInactive()} to handle the events
 * accordingly.
 * <p/>
 * When this tracker is disabled, it will no longer be listening on the
 * {@link IUserMonitorService}, but when this tracker is enabled again, it will
 * re-attach itself to the service.
 * 
 * @since 2.0
 */
public abstract class AbstractUserTracker extends AbstractTracker {

  private final IUserListener userListener = new IUserListener() {
    @Override public void onActive() {
      onUserActive();
    }

    @Override public void onInactive() {
      onUserInactive();
    }
  };

  public AbstractUserTracker() {
    super();
  }

  @Override protected void onDisable() {
    getUserMonitorService().removeListener(userListener);
  }

  @Override protected void onEnable() {
    getUserMonitorService().addListener(userListener);
  }

  /**
   * Called when user's state changes to active.
   * 
   * @see IUserListener#onActive()
   */
  protected abstract void onUserActive();

  /**
   * Called when user's state changes to inactive.
   * 
   * @see IUserListener#onInactive()
   */
  protected abstract void onUserInactive();

  /**
   * Gets the {@link IUserMonitorService} this tracker is listening on.
   * 
   * @return the service, not null
   */
  protected IUserMonitorService getUserMonitorService() {
    return (IUserMonitorService)getWorkbench()
        .getService(IUserMonitorService.class);
  }
}
