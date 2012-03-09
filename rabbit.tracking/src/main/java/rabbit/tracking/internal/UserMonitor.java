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

package rabbit.tracking.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.System.nanoTime;
import static org.eclipse.swt.SWT.KeyDown;
import static org.eclipse.swt.SWT.MouseDown;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.services.IDisposable;

import rabbit.tracking.IUserMonitor;

import com.google.common.annotations.VisibleForTesting;

public final class UserMonitor implements IUserMonitor, IDisposable {

  /** The type of events we are filtering on */
  private static final int[] FILTER_EVENTS = {KeyDown, MouseDown};

  /**
   * Starts a service.
   * 
   * @param display the display to observe on
   * @param timeout if no activity received in this amount of time, the user is
   *        considered inactive
   * @param unit the unit for the timeout
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if timeout is negative
   */
  public static UserMonitor start(Display display, long timeout,
      TimeUnit unit) {
    UserMonitor monitor = new UserMonitor(display, timeout, unit);
    monitor.start();
    return monitor;
  }

  private class EventListener implements Listener {
    @Override public void handleEvent(Event event) {
      lastEventNanos.set(nanoTime());
      if (!active.getAndSet(true)) {
        onActive();
      }
      worker.interrupt();
    }
  }

  private class WorkerThread extends Thread {
    private final AtomicBoolean terminated = new AtomicBoolean();

    @Override public void run() {
      for (;;) {
        if (terminated.get()) {
          break;
        }
        try {
          // Wait for timeout, if not interrupted, that means no user event has
          // been received, go ahead and notify listeners
          sleep(timeout);
          active.set(false);
          final long snapshotNanos = lastEventNanos.get();
          display.asyncExec(new Runnable() {
            @Override public void run() {
              if (lastEventNanos.get() == snapshotNanos) {
                onInactive();
              }
            }
          });
          // Listeners has been notified, wait until next user event
          // (interruption) then start timeout again
          for (;;) {
            sleep(Long.MAX_VALUE);
          }
        } catch (InterruptedException e) {
          // Caused by user event, restart timeout
          // TODO remove
          System.err.println("Received user event");
        }
      }
    }

    void terminate() {
      terminated.set(true);
      interrupt();
    }
  }

  private final long timeout;
  private final Display display;
  private final Listener eventListener;
  private final Set<IUserListener> listeners;

  private final AtomicLong lastEventNanos;
  private final AtomicBoolean active;
  private final AtomicBoolean started;
  private final WorkerThread worker;

  @VisibleForTesting//
  UserMonitor(Display display, long timeout, TimeUnit unit) {
    checkArgument(timeout >= 0, "timeout = " + timeout);
    this.display = checkNotNull(display, "display");
    this.timeout = checkNotNull(unit, "unit").toMillis(timeout);
    this.eventListener = new EventListener();
    this.listeners = new CopyOnWriteArraySet<IUserListener>();
    this.active = new AtomicBoolean(true);
    this.started = new AtomicBoolean();
    this.lastEventNanos = new AtomicLong();
    this.worker = new WorkerThread();
  }

  @VisibleForTesting void start() {
    if (!started.compareAndSet(false, true)) {
      throw new IllegalStateException("already started");
    }
    display.asyncExec(new Runnable() {
      @Override public void run() {
        for (int event : FILTER_EVENTS) {
          display.addFilter(event, eventListener);
        }
      }
    });
    worker.start();
  }

  /**
   * Gets the timeout in milliseconds.
   */
  public long getTimeout() {
    return timeout;
  }

  @Override public void addListener(IUserListener listener) {
    listeners.add(checkNotNull(listener, "listener"));
  }

  @Override public void removeListener(IUserListener listener) {
    listeners.remove(checkNotNull(listener, "listener"));
  }

  @Override public boolean isUserActive() {
    if (!started.get()) {
      throw new IllegalStateException("service is not started");
    }
    return active.get();
  }

  public boolean isStarted() {
    return started.get();
  }

  private void onActive() {
    for (IUserListener listener : listeners) {
      listener.onActive();
    }

    // TODO remove
    System.err.println("usermonitor2.onactive");
  }

  private void onInactive() {
    for (IUserListener listener : listeners) {
      listener.onInactive();
    }

    // TODO remove
    System.err.println("usermonitor2.oninactive");
  }

  @Override public void dispose() {
    if (!started.compareAndSet(true, false)) {
      return;
    }
    worker.terminate();
    listeners.clear();
    display.asyncExec(new Runnable() {
      @Override public void run() {
        for (int event : FILTER_EVENTS) {
          display.removeFilter(event, eventListener);
        }
      }
    });
    // TODO remove
    System.err.println("usermonitor2.dispose");
  }
}
