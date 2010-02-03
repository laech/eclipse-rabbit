package rabbit.tracking.storage.xml;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import rabbit.tracking.storage.xml.schema.EventListType;

public interface IDataStore {

	File getDataFile(Calendar date);
	
	List<File> getDataFiles(Calendar start, Calendar end);
	
	EventListType read(File f);
	
	void write(EventListType doc, File f);

	File getStorageLocation();
	
}
