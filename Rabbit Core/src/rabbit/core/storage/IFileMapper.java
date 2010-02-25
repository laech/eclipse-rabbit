package rabbit.core.storage;

import org.eclipse.core.resources.IFile;

/**
 * Manages mapping of file to id and vice versa.
 */
public interface IFileMapper {

	/**
	 * Gets the file which has the given id.
	 * 
	 * @param fileId
	 *            The id of the file.
	 * @return The file, may or may not exists in the workspace, or null if
	 *         there is no file with the given id.
	 */
	IFile getFile(String fileId);

	/**
	 * Gets the id of the given file.
	 * 
	 * @param file
	 *            The file.
	 * @return The id, or null if not found.
	 */
	String getId(IFile file);

	/**
	 * Inserts the given file into the database, if the file already exists, an
	 * existing id is returned, otherwise a new id is returned.
	 * 
	 * @param file
	 *            The file.
	 * @return An existing id if the file already exists in the database, a new
	 *         id if the file is new.
	 */
	String insert(IFile file);

}
