package rabbit.tracking.storage.xml;

import java.util.Collection;

import rabbit.tracking.event.Event;

public interface IXmlStorer<T extends Event> {

	public void insert(T e);
	
	public void insert(Collection<? extends T> col);
	
}
