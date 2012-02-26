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

import rabbit.tracking.IPersistableEventListener;

/**
 * Helper class for sending notifications to a collection of
 * {@link IPersistableEventListener}s.
 * 
 * @since 2.0
 */
public abstract class PersistableEventListenerSupport<E> {

  public PersistableEventListenerSupport() {
  }

  /**
   * Notifies the listeners that the tracker has been requested to save data.
   */
  public final void notifyOnSave() {
    for (IPersistableEventListener<? super E> listener : getListeners()) {
      listener.onSave();
    }
  }

  /**
   * Notifies the listeners with the given event.
   * 
   * @param event the event, not null
   */
  public final void notifyOnEvent(E event) {
    for (IPersistableEventListener<? super E> listener : getListeners()) {
      listener.onEvent(event);
    }
  }

  /**
   * Gets the list of listeners to send notifications to.
   * 
   * @return the listeners, or an empty collection if none
   */
  protected abstract Iterable<? extends IPersistableEventListener<? super E>> getListeners();
}
