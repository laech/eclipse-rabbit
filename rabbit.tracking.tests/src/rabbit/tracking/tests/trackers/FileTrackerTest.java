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
package rabbit.tracking.tests.trackers;

import rabbit.data.handler.DataHandler;
import rabbit.data.store.model.FileEvent;
import rabbit.tracking.internal.trackers.FileTracker;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;

/**
 * Test for {@link FileTracker}
 */
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
    assertTrue(start <= event.getTime().getMillis());
    assertTrue(end >= event.getTime().getMillis());
    assertTrue(sleepDuration <= event.getDuration());
    assertTrue((end - start) >= event.getDuration());

    bot.activeShell().close();
  }

  @Override
  protected FileEvent createEvent() {
    return new FileEvent(new DateTime(), 10, "someId");
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
      String id = DataHandler.getFileMapper().getId(file);
      return event.getFileId().equals(id);
    } else {
      return false;
    }
  }

  @Override
  protected void internalAssertAccuracy(FileEvent event, IWorkbenchPart part,
      long durationInMillis, int size, DateTime start, DateTime end) {

    // 1/10 of a second is acceptable?
    Assert.assertTrue(durationInMillis - 100 <= event.getDuration());
    Assert.assertTrue(durationInMillis + 100 >= event.getDuration());
    Assert.assertTrue(start.compareTo(event.getTime()) <= 0);
    Assert.assertTrue(end.compareTo(event.getTime()) >= 0);
    Assert.assertEquals(size, tracker.getData().size());
    IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(
        IFile.class);
    Assert.assertEquals(event.getFileId(), DataHandler.getFileMapper().getId(
        file));
  }

}
