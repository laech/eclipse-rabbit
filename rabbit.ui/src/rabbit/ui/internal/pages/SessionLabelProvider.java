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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A column label provider for a {@link SessionPage}
 */
public class SessionLabelProvider extends ColumnLabelProvider {

  private final DateLabelProvider dateLabels;
  // TODO workspace

  /**
   * Constructor.
   */
  public SessionLabelProvider() {
    this.dateLabels = new DateLabelProvider();
  }

  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
  }

  @Override
  public Image getImage(Object element) {
    return dateLabels.getImage(element);
  }

  @Override
  public String getText(Object element) {
    return dateLabels.getText(element);
  }
}
