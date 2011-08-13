/*
 * Copyright 2011 The Rabbit Eclipse Plug-in Project
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
package rabbit.data.access.model;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Contains session data information.
 * <p/>
 * Values represented by the keys defined in this interface are not null.
 */
public interface ISessionData extends IData {

  /**
   * Key for the date.
   */
  IKey<LocalDate> DATE = Keys.DATE;

  /**
   * Key for the workspace.
   */
  IKey<WorkspaceStorage> WORKSPACE = Keys.WORKSPACE;

  /**
   * Key for the duration.
   */
  IKey<Duration> DURATION = Keys.DURATION;

  /**
   * Key for the session intervals.
   */
  IKey<List<Interval>> INTERVALS = Keys.INTERVALS;
}
