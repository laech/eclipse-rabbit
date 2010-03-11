/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core.internal.storage.xml;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import rabbit.core.internal.storage.xml.schema.events.EventListType;

/**
 * Represents a data store for storing data.
 */
public interface IDataStore {

	/**
	 * Gets the data file for the given date of the current workspace.
	 * 
	 * @param date
	 *            The date.
	 * @return The file, this file may not be physically existing.
	 */
	File getDataFile(Calendar date);

	/**
	 * Gets the data file for the given date in the given location.
	 * 
	 * @param date
	 *            The date.
	 * @param location
	 *            The folder location.
	 * @return The file, this file may not be physically existing.
	 */
	File getDataFile(Calendar date, IPath location);

	/**
	 * Gets the data files for between the given dates, inclusively, of all the
	 * workspaces.
	 * 
	 * @param start
	 *            The start date.
	 * @param end
	 *            The end date.
	 * @return A list of files that are physically existing across all
	 *         workspaces.
	 */
	List<File> getDataFiles(Calendar start, Calendar end);

	/**
	 * Gets the storage location, if the location does not exist, it will be
	 * created.
	 * 
	 * @return The storage location.
	 */
	IPath getStorageLocation();

	/**
	 * Creates the data from a given file.
	 * 
	 * @param f
	 *            The file to read from.
	 * @return An {@link EventListType} object with data, or an empty one if the
	 *         file does not contain one.
	 */
	 EventListType read(File f);

	/**
	 * Writes the given element to the file.
	 * 
	 * @param doc
	 *            The element.
	 * @param f
	 *            The file.
	 */
	boolean write(EventListType doc, File f);

}
