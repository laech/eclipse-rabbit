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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.IKey;
import rabbit.data.access.model.WorkspaceStorage;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.resources.IFile;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Contains information about time spent on a file.
 */
public class FileData implements IFileData {

  /**
   * Immutable map of data.
   */
  private final Map<IKey<? extends Object>, Object> data;

  /**
   * Constructor.
   * 
   * @param date The date of the session.
   * @param workspace The workspace of the session.
   * @param duration The duration of the session.
   * @param file The workspace file.
   * @param time The time of the session (this is nullable for backward
   *        compatibility reason).
   * @throws NullPointerException If {@code date}, {@code workspace},
   *         {@code duration}, or {@code file} is <code>null</code>.
   */
  public FileData(
      LocalDate date,
      WorkspaceStorage workspace,
      Duration duration,
      IFile file,
      @Nullable LocalTime time) {

    final float loadFactor = 1;
    final int capacity = time == null ? 4 : 5;
    data = new HashMap<IKey<? extends Object>, Object>(capacity, loadFactor);
    data.put(DATE, checkNotNull(date, "date"));
    data.put(WORKSPACE, checkNotNull(workspace, "workspace"));
    data.put(DURATION, checkNotNull(duration, "duration"));
    data.put(FILE, checkNotNull(file, "file"));
    if (time != null) {
      data.put(TIME, time);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(@Nullable IKey<T> key) {
    return (T)data.get(key);
  }
}
