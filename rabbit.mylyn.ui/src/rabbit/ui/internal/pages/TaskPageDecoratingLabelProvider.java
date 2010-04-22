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
package rabbit.ui.internal.pages;

import static rabbit.ui.internal.util.DurationFormat.format;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A decorating label provider for a {@link TaskPage}.
 */
public class TaskPageDecoratingLabelProvider extends DecoratingLabelProvider
    implements ITableLabelProvider {

  private final TaskPageContentProvider contents;

  /**
   * Constructs a new label provider.
   * @param contents The content provider.
   */
  public TaskPageDecoratingLabelProvider(TaskPageContentProvider contents) {
    super(new TaskPageLabelProvider(), null);
    checkNotNull(contents);
    this.contents = contents;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    if (columnIndex == 0) {
      return super.getImage(element);
    }
    return null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    switch (columnIndex) {
    case 0:
      return super.getText(element);
    case 1:
      if (contents.shouldPaint(element))
        return format(contents.getValue(element));
    default:
      return null;
    }
  }
}