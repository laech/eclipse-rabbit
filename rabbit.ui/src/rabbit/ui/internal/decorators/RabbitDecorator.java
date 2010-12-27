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
package rabbit.ui.internal.decorators;

import rabbit.data.access.model.WorkspaceStorage;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.joda.time.LocalDate;

public class RabbitDecorator
    extends BaseLabelProvider implements ILightweightLabelDecorator {
  
  private static final String SEPARATOR = System.getProperty("file.separator");

  private LocalDate today;
  private long endOfToday;

  public RabbitDecorator() {
    checkDateFields();
  }

  @Override
  public void decorate(Object element, IDecoration decoration) {
    if (element instanceof LocalDate) {
      decorateDate((LocalDate) element, decoration);
    } else if (element instanceof WorkspaceStorage) {
      decorateWorkspace((WorkspaceStorage) element, decoration);
    }
  }

  private void checkDateFields() {
    if (System.currentTimeMillis() > endOfToday) {
      today = new LocalDate();
      endOfToday = today.toInterval().getEndMillis();
    }
  }

  private void decorateDate(LocalDate date, IDecoration decoration) {
    checkDateFields();
    if (date.getYear() == today.getYear()) {
      switch (today.getDayOfYear() - date.getDayOfYear()) {
      case 0:
        decoration.addSuffix(" [Today]");
        break;
      case 1:
        decoration.addSuffix(" [Yesterday]");
        break;
      default:
        break;
      }
    }
  }

  private void decorateWorkspace(WorkspaceStorage ws, IDecoration decoration) {
    IPath path = ws.getWorkspacePath();
    if (path != null) {
      decoration.addSuffix(" [" + path.toOSString() + "]");
    } else {
      String guess = ws.getStoragePath().lastSegment().replace(".", SEPARATOR);
      decoration.addSuffix(" [may be " + guess + "?]");
    }
  }
}
