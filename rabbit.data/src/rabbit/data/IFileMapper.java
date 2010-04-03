package rabbit.data;

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

import org.eclipse.core.resources.IFile;

/**
 * Manages mapping of file to id and vice versa.
 */
public interface IFileMapper {

  /**
   * Gets the file of the given file id from an external source. This method may
   * returns a file if {@link #getFile(String)} returns null.
   * 
   * @param fileId The id of the file.
   * @return The file, or null if not found.
   */
  IFile getExternalFile(String fileId);

  /**
   * Gets the file which has the given id.
   * 
   * @param fileId The id of the file.
   * @return The file, or null if not found.
   */
  IFile getFile(String fileId);

  /**
   * Gets the id of the given file.
   * 
   * @param file The file.
   * @return The id, or null if not found.
   */
  String getId(IFile file);

  /**
   * Inserts the given file into the database, if the path already exists, an
   * existing id is returned, otherwise a new id is returned.
   * 
   * @param file The file.
   * @return An existing id if the file already exists in the database, a new id
   *         if the file is new.
   */
  String insert(IFile file);

  // TODO
  boolean write(boolean updateExternalDb);

}
