/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.tasks.core.internal.trackers;

import java.util.Calendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.core.RabbitCore;
import rabbit.core.internal.trackers.AbstractPartTracker;
import rabbit.core.storage.IStorer;
import rabbit.tasks.core.TaskCore;
import rabbit.tasks.core.events.TaskEvent;

/**
 * Tracks task events.
 */
public class TaskTracker extends AbstractPartTracker<TaskEvent> {
	
	public TaskTracker() {
		super();
	}

	@Override
	protected TaskEvent tryCreateEvent(Calendar time, long duration, IWorkbenchPart p) {
		ITask task = TasksUi.getTaskActivityManager().getActiveTask();
		if (task == null) {
			return null;
		}

		if (p instanceof IEditorPart == false) {
			return null;
		}

		IFile file = (IFile) ((IEditorPart) p).getEditorInput().getAdapter(IFile.class);
		if (file == null) {
			return null;
		}

		String fileId = RabbitCore.getFileMapper().insert(file);
		return new TaskEvent(time, duration, fileId, task);
	}

	@Override
	protected IStorer<TaskEvent> createDataStorer() {
		return TaskCore.getTaskEventStorer();
	}

}