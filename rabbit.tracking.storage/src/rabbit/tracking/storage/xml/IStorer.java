package rabbit.tracking.storage.xml;

import java.util.Collection;

public interface IStorer<T> {

	void insert(T e);
	
	void insert(Collection<? extends T> col);

	void commit();
	
	IDataStore getDataStore();
	
}
