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

import rabbit.ui.internal.AbstractTreeContentProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

/**
 * A content provider that can categorize the data by date.
 */
public abstract class AbstractDateCategoryContentProvider extends
    AbstractTreeContentProvider {

  private boolean displayByDate;

  /** The page of this content provider. */
  @Nonnull
  protected AbstractTreeViewerPage page;

  /**
   * Constructor.
   * 
   * @param page The parent of this content provider.
   * @param displayByDate True to display the data by date.
   * @throws NullPointerException If page is null.
   */
  public AbstractDateCategoryContentProvider(
      @Nonnull AbstractTreeViewerPage page, boolean displayByDate) {
    checkNotNull(page);
    this.page = page;
    this.displayByDate = displayByDate;
  }

  /**
   * Checks whether the data is categorized by dates.
   * 
   * @return True if the data is categorized by dates, false otherwise.
   */
  public boolean isDisplayingByDate() {
    return displayByDate;
  }

  /**
   * Sets whether the data is categorized by dates.
   * 
   * @param displayByDate True to set to display by dates, false otherwise.
   */
  public void setDisplayByDate(boolean displayByDate) {
    if (isDisplayingByDate() == displayByDate)
      return;

    this.displayByDate = displayByDate;
    updateMaxValue();

    page.getViewer().getTree().setRedraw(false);
    page.getViewer().refresh(true);
    page.getViewer().getTree().setRedraw(true);
  }

  /**
   * Updates the max value for painting the cells.
   */
  protected abstract void updateMaxValue();
}
