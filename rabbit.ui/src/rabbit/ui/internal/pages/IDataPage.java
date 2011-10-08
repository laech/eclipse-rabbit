/*
 * Copyright 2011 The Rabbit Eclipse Plug-in Project
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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

import java.util.Collection;

/**
 * Represents a page to displaying a set of data.
 */
public interface IDataPage<T> {

  /**
   * Creates the content of this page.
   * 
   * @param parent the parent composite to use
   */
  void createContents(Composite parent);

  /**
   * Creates the tool bar items for this page.
   * 
   * @return all the items created
   */
  IContributionItem[] createToolBarItems();

  /**
   * Gets the display name of this page.
   * 
   * @return the name of this page
   */
  String getName();

  /**
   * Restores the state of this page from the given memento.
   * 
   * @param memento the object containing the state of this page
   */
  void onRestoreState(IMemento memento);

  /**
   * Saves the state of this page to the given memento.
   * 
   * @param memento the object to save the state to
   */
  void onSaveState(IMemento memento);

  /**
   * Updates the display of this page from the given data
   * 
   * @param data the new data to display
   */
  void update(Collection<T> data);
}
