package rabbit.tracking.storage.xml;

import java.util.Collection;

/**
 * Represents a storer that stores a particular type of objects.
 * 
 * @param <T> The object type.
 */
public interface IStorer<T> {

	/**
	 * Inserts an object to be stored.
	 * 
	 * @param e The object.
	 */
	void insert(T e);

	/**
	 * Insert a collection of objects to be stored.
	 * 
	 * @param col The collection of objects.
	 */
	void insert(Collection<? extends T> col);

	/**
	 * Stores the data.
	 */
	void commit();

}
