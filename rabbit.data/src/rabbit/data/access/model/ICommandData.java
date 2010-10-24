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
package rabbit.data.access.model;


import org.eclipse.core.commands.Command;
import org.joda.time.LocalDate;

/**
 * Contains command execution information.
 */
public interface ICommandData extends IData {
  
  /**
   * Key for the date.
   */
  static final IKey<LocalDate> DATE = Key.create();
  
  /**
   * Key for the workspace.
   */
  static final IKey<WorkspaceStorage> WORKSPACE = Key.create();
  
  /**
   * Key for the command ID.
   */
  static final IKey<Command> COMMAND = Key.create();
  
  /**
   * Key for the execution count.
   */
  static final IKey<Integer> COUNT = Key.create();
}
