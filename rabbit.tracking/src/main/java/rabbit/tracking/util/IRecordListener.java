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

/**
 * Listener to be notified of recording events.
 * 
 * @see Recorder
 * @see Record
 * @since 2.0
 */
public interface IRecordListener {

  /**
   * Called when a new record is available.
   * 
   * @param record the new record, not null
   */
  void onRecord(Record record);
}