/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
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

import rabbit.data.handler.DataHandler;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.TaskFileEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.DateTime;

/**
 * Tracks task events.
 */
public class TaskTracker extends AbstractPartTracker<TaskFileEvent> {

  public TaskTracker() {
    super();
  }

  @Override
  protected IStorer<TaskFileEvent> createDataStorer() {
    return DataHandler.getStorer(TaskFileEvent.class);
  }

  @Override
  protected TaskFileEvent tryCreateEvent(DateTime endTime, long duration, IWorkbenchPart part) {
    ITask task = TasksUi.getTaskActivityManager().getActiveTask();
    if (task == null)
      return null;

    if (!(part instanceof IEditorPart))
      return null;

    IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
    if (file == null)
      return null;

    String fileId = DataHandler.getFileStore().insert(file);
    return new TaskFileEvent(endTime, duration, fileId, task);
  }
}
