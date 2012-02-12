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

package rabbit.tracking.util;

import java.util.Collection;

import rabbit.tracking.ITrackerListener;

/**
 * Helper class for sending notifications to a collection of
 * {@link ITrackerListener}s.
 * 
 * @since 2.0
 */
public abstract class TrackerListenerSupport<E> {

  public TrackerListenerSupport() {
  }

  /**
   * Notifies the listeners that the tracker has been requested to save data.
   */
  public final void notifyOnSaveData() {
    for (ITrackerListener<? super E> listener : getListeners()) {
      listener.onSaveData();
    }
  }

  /**
   * Notifies the listeners that the tracker is enabled.
   */
  public final void notifyOnEnabled() {
    for (ITrackerListener<? super E> listener : getListeners()) {
      listener.onEnabled();
    }
  }

  /**
   * Notifies the listeners that the tracker is disabled.
   */
  public final void notifyOnDisabled() {
    for (ITrackerListener<? super E> listener : getListeners()) {
      listener.onDisabled();
    }
  }

  /**
   * Notifies the listeners with the given event.
   * 
   * @param event the event, not null
   */
  public final void notifyOnEvent(E event) {
    for (ITrackerListener<? super E> listener : getListeners()) {
      listener.onEvent(event);
    }
  }

  /**
   * Gets the list of listeners to send notifications to.
   * 
   * @return the listeners, or an empty collection if none
   */
  protected abstract Collection<? extends ITrackerListener<? super E>> getListeners();
}
