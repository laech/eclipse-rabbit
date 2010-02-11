package rabbit.core.internal.storage.xml;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import rabbit.core.internal.storage.xml.schema.events.EventListType;

/**
 * Represents a data store for storing data.
 */
public interface IDataStore {

	/**
	 * Gets the data file for the given date.
	 * 
	 * @param date
	 *            The date.
	 * @return The file, this file may not be physically existing.
	 */
	File getDataFile(Calendar date);

	/**
	 * Gets the data files for between the given dates, inclusively.
	 * 
	 * @param start
	 *            The start date.
	 * @param end
	 *            The end date.
	 * @return A list of files that are physically existing.
	 */
	List<File> getDataFiles(Calendar start, Calendar end);

	/**
	 * Creates the data from a given file.
	 * 
	 * @param f
	 *            The file to read from.
	 * @return The root of the document.
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
	void write(EventListType doc, File f);

	/**
	 * Gets the storage location, if the location does not exist, it will be
	 * created.
	 * 
	 * @return The storage location.
	 */
	File getStorageLocation();

}
