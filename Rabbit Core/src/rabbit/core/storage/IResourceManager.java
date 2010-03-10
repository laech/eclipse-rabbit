package rabbit.core.storage;

import org.eclipse.core.resources.IResource;

/**
 * Manages mapping of file paths to id and vice versa.
 */
public interface IResourceManager {

	/**
	 * Gets the full path of the file has the given id.
	 * 
	 * @param fileId
	 *            The id of the file.
	 * @return The full path, in the form of
	 *         <tt>{@link IResource#getFullPath()}.toString()</tt>, or null if not
	 *         found.
	 */
	String getPath(String fileId);

	/**
	 * Gets the id of the given file path.
	 * 
	 * @param filePath
	 *            The full path of the file, in the form of
	 *            <tt>{@link IResource#getFullPath()}.toString()</tt>.
	 * @return The id, or null if not found.
	 */
	String getId(String filePath);

	/**
	 * Inserts the given file path into the database, if the path already
	 * exists, an existing id is returned, otherwise a new id is returned.
	 * 
	 * @param filePath
	 *            The full path of the file, in the form of
	 *            <tt>{@link IResource#getFullPath()}.toString()</tt>.
	 * @return An existing id if the path already exists in the database, a new
	 *         id if the path is new.
	 */
	String insert(String filePath);

	/**
	 * Gets the file path of the given file id from an external source. 
	 * This method may returns a path if {@link #getPath(String)} returns null.
	 * 
	 * @param fileId The id of the file.
	 * @return The file path, or null if not found.
	 */
	String getExternalPath(String fileId);

}
