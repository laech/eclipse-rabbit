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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import rabbit.core.internal.RabbitCorePlugin;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

/**
 * Data stores.
 */
public enum DataStore implements IDataStore {

	COMMAND_STORE("commandEvents"),
	PART_STORE("partEvents"),
	PERSPECTIVE_STORE("perspectiveEvents"),
	FILE_STORE("fileEvents"),

	/**
	 * @since 1.1
	 */
	TASK_STORE("taskEvents"),
	
	/**
	 * @since 1.1
	 */
	LAUNCH_STORE("launchEvents");
	

	/**
	 * Formats a date into "yyyy-MM".
	 */
	private final DateFormat monthFormatter = new SimpleDateFormat("yyyy-MM");

	/**
	 * An object factory for creating XML object types.
	 */
	private final ObjectFactory objectFactory = new ObjectFactory();

	private String id;

	private DataStore(String id) {
		this.id = id;
	}

	@Override
	public File getDataFile(Calendar date) {
		return getDataFile(date, getStorageLocation());
	}

	@Override
	public File getDataFile(Calendar date, IPath location) {
		return location.append(id + "-" + monthFormatter.format(date.getTime()))
				.addFileExtension("xml").toFile();
	}

	@Override
	public List<File> getDataFiles(Calendar startDate, Calendar endDate) {
		Calendar start = (Calendar) startDate.clone();
		start.set(Calendar.DAY_OF_MONTH, 1);

		Calendar end = (Calendar) endDate.clone();
		end.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH));

		List<File> result = new ArrayList<File>();
		IPath[] storagePaths = RabbitCorePlugin.getDefault().getStoragePaths();
		while (start.compareTo(end) <= 0) {

			for (IPath path : storagePaths) {
				File f = getDataFile(start, path);
				if (f.exists()) {
					result.add(f);
				}
			}

			start.add(Calendar.MONTH, 1);
		}
		return result;
	}

	@Override
	public IPath getStorageLocation() {
		IPath path = RabbitCorePlugin.getDefault().getStoragePath();
		File f = path.toFile();
		if (!f.exists()) {
			if (!f.mkdirs()) {
				RabbitCorePlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, RabbitCorePlugin.PLUGIN_ID,
								"Unable to create storage location. Perhaps no write permission?\n"
										+ f.getAbsolutePath()));
			}
		}
		return path;
	}

	@Override
	public EventListType read(File file) {
		try {
			if (file.exists()) {
				Object obj = JaxbUtil.unmarshal(file);
				if (obj instanceof JAXBElement<?>) {
					JAXBElement<?> element = (JAXBElement<?>) obj;
					if (element.getValue() instanceof EventListType) {
						return (EventListType) element.getValue();
					}
				}
			}

		} catch (JAXBException e) {
			return objectFactory.createEventListType();
		} catch (Exception e) {
			// XML file not valid?
			
			RabbitCorePlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, RabbitCorePlugin.PLUGIN_ID, e.getMessage(), e));
			return objectFactory.createEventListType();
		}
		return objectFactory.createEventListType();
	}

	@Override
	public boolean write(EventListType doc, File f) {
		if (doc == null || f == null) {
			throw new NullPointerException();
		}
		try {
			JaxbUtil.marshal(objectFactory.createEvents(doc), f);
			return true;
		} catch (JAXBException e) {
			RabbitCorePlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, RabbitCorePlugin.PLUGIN_ID,
							"Unable to save data.", e));
			return false;
		}
	}

}
