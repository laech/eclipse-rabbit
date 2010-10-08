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

import rabbit.data.access.IAccessor;
import rabbit.ui.IProvider;
import rabbit.ui.Preference;

import org.eclipse.core.runtime.jobs.Job;

import java.util.Collection;

/**
 * An internal abstract page, utilizing the {@link IProvider} interface.
 */
abstract class InternalPage<T> extends AbstractFilteredTreePage
    implements IProvider<T> {

  private Collection<T> data;

  /**
   * Constructor.
   */
  public InternalPage() {
  }

  @Override
  public Collection<T> get() {
    return data;
  }

  @Override
  public Job updateJob(Preference pref) {
    return new UpdateJob<T>(getViewer(), pref, getAccessor()) {
      @Override
      protected Object getInput(Collection<T> c) {
        data = c;
        return InternalPage.this;
      }
    };
  }

  /**
   * Gets the accessor for getting the data.
   * 
   * @return
   */
  protected abstract IAccessor<T> getAccessor();
}
