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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.junit.Before;
import org.junit.runner.RunWith;

import rabbit.core.RabbitCore;
import rabbit.core.internal.trackers.AbstractPartTrackerTest;
import rabbit.tasks.core.events.TaskEvent;

/**
 * @see TaskTracker
 */
@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public class TaskTrackerTest extends AbstractPartTrackerTest<TaskEvent> {

	private ITask task;

	@Before
	public void setUpActiveTask() {
		task = new LocalTask(System.currentTimeMillis() + "", "what?");
		task.setCreationDate(new Date());
		TasksUi.getTaskActivityManager().activateTask(task);
	}

	@Override
	protected TaskEvent createEvent() {
		return new TaskEvent(Calendar.getInstance(), 187, "fileId", task);
	}

	@Override
	protected TaskTracker createTracker() {
		return new TaskTracker();
	}

	@Override
	protected boolean hasSamePart(TaskEvent event, IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) part;
			IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
			String id = RabbitCore.getFileMapper().getId(
					file.getFullPath().toString());
			return event.getFileId().equals(id);
		} else {
			return false;
		}
	}

	@Override
	protected void internalAssertAccuracy(TaskEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		// 1/10 of a second is acceptable?
		assertTrue(durationInMillis - 100 <= event.getDuration());
		assertTrue(durationInMillis + 100 >= event.getDuration());
		assertTrue(start.compareTo(event.getTime()) <= 0);
		assertTrue(end.compareTo(event.getTime()) >= 0);
		assertEquals(size, tracker.getData().size());
		IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
		assertEquals(event.getFileId(), RabbitCore.getFileMapper().getId(
				file.getFullPath().toString()));
		
		assertEquals(task, event.getTask());
	}

}
