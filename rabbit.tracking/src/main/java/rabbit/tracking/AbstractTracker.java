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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implements common tracker behaviors.
 * 
 * @since 2.0
 */
public abstract class AbstractTracker implements ITracker {

  private final AtomicBoolean enabled;

  /** Constructor for subclass. */
  protected AbstractTracker() {
    enabled = new AtomicBoolean(false);
  }

  @Override public boolean isEnabled() {
    return enabled.get();
  }

  @Override public void setEnabled(boolean enable) {
    if (enabled.compareAndSet(!enable, enable)) {
      if (enable) {
        onEnable();
      } else {
        onDisable();
      }
    }
  }

  /**
   * Called when this tracker is being enabled.
   * <p/>
   * This method will only be called if the tracker was previously disabled.
   * That means if the tracker is already enabled, calling
   * {@code setEnabled(true)} again will not trigger this method to be called.
   */
  protected abstract void onEnable();

  /**
   * Called when this tracker is being disabled.
   * <p/>
   * This method will only be called if the tracker was previously enabled. That
   * means if the tracker is already disabled, calling {@code setEnabled(false)}
   * again will not trigger this method to be called.
   */
  protected abstract void onDisable();
}
