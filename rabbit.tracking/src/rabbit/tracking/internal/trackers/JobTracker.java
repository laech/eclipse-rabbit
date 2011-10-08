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
package rabbit.tracking.internal.trackers;

import rabbit.tracking.ITracker;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

import static java.util.Collections.emptyList;

import java.util.Collection;

/**
 * Tracks workbench {@link Job}s
 */
public final class JobTracker implements ITracker<Void>, IJobChangeListener {

  private boolean enabled;

  @Override
  public void flushData() {
  }

  @Override
  public Collection<Void> getData() {
    return emptyList();
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void saveData() {
  }

  @Override
  public void setEnabled(boolean enable) {
    this.enabled = enable;
    if (enable) {
      Job.getJobManager().addJobChangeListener(this);
    } else {
      Job.getJobManager().removeJobChangeListener(this);
    }
  }

  @Override
  public void aboutToRun(IJobChangeEvent event) {
//    System.out.println("aboutToRun: " + event.getJob().getName());
  }

  @Override
  public void awake(IJobChangeEvent event) {
//    System.out.println("awake: " + event.getJob().getName());
  }

  @Override
  public void done(IJobChangeEvent event) {
    Job job = event.getJob();
    System.out.println("done: " + job.getName());
    IStatus result = job.getResult();
    if (result != null) {
      System.out.println("\t" + result.getPlugin());
    }
  }

  @Override
  public void running(IJobChangeEvent event) {
//    System.out.println("running: " + event.getJob().getName());
  }

  @Override
  public void scheduled(IJobChangeEvent event) {
//    System.out.println("scheduled: " + event.getJob().getName());
  }

  @Override
  public void sleeping(IJobChangeEvent event) {
    System.out.println("sleeping: " + event.getJob().getName());
  }
}
