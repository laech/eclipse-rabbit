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
 * <p/>
 * This implementation is thread safe.
 * 
 * @since 2.0
 */
public abstract class AbstractTracker implements ITracker {

  private final AtomicBoolean enabled;

  protected AbstractTracker() {
    enabled = new AtomicBoolean(false);
  }

  @Override public final boolean isStarted() {
    return enabled.get();
  }

  @Override public final void start() {
    if (enabled.compareAndSet(false, true)) {
      onStart();
    }
  }

  @Override public final void stop() {
    if (enabled.compareAndSet(true, false)) {
      onStop();
    }
  }

  /**
   * Called when this tracker is being started.
   * <p/>
   * This method will only be called if the tracker was previously stopped. That
   * means if the tracker is already started, calling {@link #start()} again
   * will not trigger this method to be called.
   */
  protected abstract void onStart();

  /**
   * Called when this tracker is being stopped.
   * <p/>
   * This method will only be called if the tracker was previously started. That
   * means if the tracker is already stopped, calling {@link #stop()} again will
   * not trigger this method to be called.
   */
  protected abstract void onStop();
}
