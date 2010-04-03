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
package rabbit.ui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

/**
 * Represents a page for displaying graphical information.
 */
public interface IPage {

  /**
   * Creates the content of this page.
   * 
   * @param parent The parent composite.
   */
  void createContents(Composite parent);

  /**
	 * 
	 */
  IContributionItem[] createToolBarItems(IToolBarManager toolBar);

  /**
   * Updates the data of this page.
   * 
   * @param preference The object containing the update preferences.
   */
  void update(DisplayPreference preference);

}
