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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Iterator;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.junit.Test;
import org.junit.runner.RunWith;

import rabbit.core.RabbitCore;
import rabbit.core.events.FileEvent;

/**
 * Test for {@link FileTracker}
 */
@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public class FileTrackerTest extends AbstractPartTrackerTest<FileEvent> {

	@Test
	public void testNewWindow() {
		long sleepDuration = 15;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		openNewWindow();
		IEditorPart editor = openNewEditor();
		uiSleep(sleepDuration);
		openNewEditor();
		long end = System.currentTimeMillis();

		// One for the original window,
		// one for the newly opened window's default active view,
		// But both are views, not editors,so they are not added,
		// one for the newly opened editor.
		assertEquals(1, tracker.getData().size());

		Iterator<FileEvent> it = tracker.getData().iterator();
		FileEvent event = it.next();
		assertTrue(hasSamePart(event, editor));
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue((end - start) >= event.getDuration());

		bot.activeShell().close();
	}

	@Test
	public void testTask() {
		tracker.setEnabled(false);
		
		ITask task = new LocalTask("id", "summary");
		ITaskActivityManager taskManager = TasksUi.getTaskActivityManager();
		taskManager.deactivateActiveTask();

		IWorkbenchPartReference ref = getActiveWindow().getPartService().getActivePartReference();
		if (!(ref instanceof IEditorReference)) {
			openNewEditor();
			ref = getActiveWindow().getPartService().getActivePartReference();
		}

		taskManager.activateTask(task);
		tracker.setEnabled(true);
		uiSleep(20);
		tracker.setEnabled(false);
		assertEquals(1, tracker.getData().size());
		
		FileEvent event = tracker.getData().iterator().next();
		ITask taskRef = event.getTask();
		assertNotNull(taskRef);
		assertEquals(task.getRepositoryUrl(), taskRef.getRepositoryUrl());
		assertEquals(task.getTaskId(), taskRef.getTaskId());
	}

	@Override
	protected FileEvent createEvent() {
		return new FileEvent(Calendar.getInstance(), 10, "someId");
	}

	@Override
	protected FileTracker createTracker() {
		return new FileTracker();
	}

	@Override
	protected boolean hasSamePart(FileEvent event, IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) part;
			IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
			String id = RabbitCore.getDefault().getResourceManager().getId(
					file.getFullPath().toString());
			return event.getFileId().equals(id);
		} else {
			return false;
		}
	}

	@Override
	protected void internalAssertAccuracy(FileEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		// 1/10 of a second is acceptable?
		Assert.assertTrue(durationInMillis - 100 <= event.getDuration());
		Assert.assertTrue(durationInMillis + 100 >= event.getDuration());
		Assert.assertTrue(start.compareTo(event.getTime()) <= 0);
		Assert.assertTrue(end.compareTo(event.getTime()) >= 0);
		Assert.assertEquals(size, tracker.getData().size());
		IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
		Assert.assertEquals(event.getFileId(), RabbitCore.getDefault().getResourceManager().getId(
				file.getFullPath().toString()));
	}

}
