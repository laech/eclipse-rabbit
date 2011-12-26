/*
 * Copyright 2011 The Rabbit Eclipse Plug-in Project
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

import rabbit.data.access.IAccessor;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * A job that retrieves data in the background and sends the result back on the
 * UI thread.
 */
public abstract class AsyncJob<T> extends Job {

  private final IAccessor<T> accessor;
  private final LocalDate start;
  private final LocalDate end;

  /**
   * Creates a new job.
   * 
   * @param accessor the accessor to retrieve data
   * @param start the start date of the data to be retrieved
   * @param end the end date of the data to be retrieved
   * @throws NullPointerException if any argument is {@code null}
   */
  public AsyncJob(IAccessor<T> accessor, LocalDate start, LocalDate end) {
    super("Updating Rabbit View...");
    this.accessor = checkNotNull(accessor, "accessor");
    this.start = checkNotNull(start, "start");
    this.end = checkNotNull(end, "end");
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    if (monitor.isCanceled()) {
      return Status.CANCEL_STATUS;
    } else {
      monitor.beginTask("Updating page...", 1);
    }

    final Collection<T> data = accessor.getData(start, end);
    if (monitor.isCanceled()) {
      return Status.CANCEL_STATUS;
    }
    onFinishBackground(data);
    getWorkbench().getDisplay().asyncExec(new Runnable() {
      @Override
      public void run() {
        onFinishUi(data);
      }
    });
    monitor.worked(1);
    monitor.done();
    return Status.OK_STATUS;
  }

  /**
   * Called on the UI thread when done, after calling
   * {@link #onFinishBackground(Collection)}.
   * 
   * @param data the data retrieved
   */
  protected void onFinishUi(Collection<T> data) {
  }

  /**
   * Called on the background thread when done, before calling
   * {@link #onFinishUi(Collection)}.
   * 
   * @param data the data retrieved
   */
  protected void onFinishBackground(Collection<T> data) {
  }
}
