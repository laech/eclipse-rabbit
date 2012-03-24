/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.tracking;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableSet;
import static rabbit.tracking.internal.util.Arrays.checkedCopyAsList;
import static rabbit.tracking.internal.util.Sets.newCopyOnWriteSet;

import java.util.Set;

/**
 * Implements common behaviors of a listenable tracker (adding/removing of
 * listeners).
 * <p/>
 * This implementation is thread safe.
 * 
 * @since 2.0
 */
public abstract class AbstractListenableTracker<T>
    extends AbstractTracker implements IListenableTracker<T> {

  private final Set<T> listeners;

  /**
   * Constructs a tracker with default listeners.
   * 
   * @param listeners the default listeners to be attached.
   * @throws NullPointerException if listeners contain null
   */
  protected AbstractListenableTracker(T... listeners) {
    this.listeners = newCopyOnWriteSet(checkedCopyAsList(listeners));
  }

  @Override public final void addListener(T listener) {
    listeners.add(checkNotNull(listener, "listener"));
  }

  @Override public final void removeListener(T listener) {
    listeners.remove(checkNotNull(listener, "listener"));
  }

  /**
   * Gets the listeners for iteration.
   * 
   * @return the current set of listeners, the returned collection is not
   *         modifiable
   */
  protected final Set<T> getListeners() {
    return unmodifiableSet(listeners);
  }
}
