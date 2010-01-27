package rabbit.tracking.storage.xml;

import java.util.Collection;

public interface IStorer<T> {

	public void insert(T e);
	
	public void insert(Collection<? extends T> col);

	public void commit();
	
}
