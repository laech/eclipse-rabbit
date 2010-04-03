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
import rabbit.data.store.model.FileEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;

import java.util.Calendar;

public class FileTracker extends AbstractPartTracker<FileEvent> {

  public FileTracker() {
    super();
  }

  @Override
  protected IStorer<FileEvent> createDataStorer() {
    return DataHandler.getStorer(FileEvent.class);
  }

  @Override
  protected FileEvent tryCreateEvent(Calendar time, long duration,
      IWorkbenchPart p) {
    if (p instanceof IEditorPart) {
      IEditorInput input = ((IEditorPart) p).getEditorInput();

      if (input instanceof IFileEditorInput) {
        IFile file = ((IFileEditorInput) input).getFile();
        String id = DataHandler.getFileMapper().insert(file);
        return new FileEvent(time, duration, id);

      }
    }
    return null;
  }
}
