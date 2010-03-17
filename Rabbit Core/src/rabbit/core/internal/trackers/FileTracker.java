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
package rabbit.core.internal.trackers;

import java.util.Calendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.core.RabbitCore;
import rabbit.core.events.FileEvent;
import rabbit.core.storage.IStorer;

public class FileTracker extends AbstractPartTracker<FileEvent> {

	private boolean isMylynInstalled = false;

	public FileTracker() {
		super();

		try {
			TasksUi.getTaskActivityManager();
			isMylynInstalled = true;
		} catch (NoClassDefFoundError e) {
			isMylynInstalled = false;
		}
	}

	@Override
	protected IStorer<FileEvent> createDataStorer() {
		return RabbitCore.getStorer(FileEvent.class);
	}

	@Override
	protected FileEvent tryCreateEvent(Calendar time, long duration, IWorkbenchPart p) {
		FileEvent event = null;
		if (p instanceof IEditorPart) {
			IFile f = (IFile) ((IEditorPart) p).getEditorInput().getAdapter(IFile.class);
			if (f == null) {
				return event;
			}

			String id = RabbitCore.getDefault().getResourceManager().insert(
					f.getFullPath().toString());
			event = new FileEvent(time, duration, id);
			if (isMylynInstalled) {
				event.setTask(TasksUi.getTaskActivityManager().getActiveTask());
			}
		}
		return event;
	}
}
