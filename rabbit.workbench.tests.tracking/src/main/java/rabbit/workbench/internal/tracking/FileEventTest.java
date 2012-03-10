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
package rabbit.workbench.internal.tracking;

import static org.eclipse.core.runtime.Path.fromPortableString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.Duration.standardHours;
import static org.joda.time.Instant.now;

import org.eclipse.core.runtime.IPath;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import rabbit.tracking.TimedEventTest;
import rabbit.workbench.internal.tracking.FileEvent;

public final class FileEventTest extends TimedEventTest {

  private final Instant instant = now();
  private final Duration duration = standardHours(2);
  private final IPath path = fromPortableString("/project/folder/file");

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfFilePathIsNull() {
    create(instant, duration, null);
  }

  @Test public void returnsTheFilePath() {
    assertThat(create(instant, duration, path).file(), is(path));
  }

  @Override protected final FileEvent create(Instant instant, Duration duration) {
    return create(instant, duration, path);
  }

  private FileEvent create(Instant instant, Duration duration, IPath filePath) {
    return new FileEvent(instant, duration, filePath);
  }
}
