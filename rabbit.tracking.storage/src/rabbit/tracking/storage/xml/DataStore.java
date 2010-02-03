package rabbit.tracking.storage.xml;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBException;

import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.ObjectFactory;

public enum DataStore implements IDataStore {
	
	COMMAND_STORE     ("commandEvents"),
	PART_STORE        ("partEvents"),
	PERSPECTIVE_STORE ("perspectiveEvents"),
	FILE_STORE        ("fileEvents");
	
	/**
	 * Formats a date into "yyyy-MM".
	 */
	private static final DateFormat MONTH_FORMATTER = new SimpleDateFormat("yyyy-MM");

	/**
	 * An object factory for creating XML object types.
	 */
	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
	
	private String id;
	
	DataStore(String identifier) {
		id = identifier;
	}

	@Override
	public File getDataFile(Calendar date) {
		StringBuilder builder = new StringBuilder();
		builder.append(getStorageLocation().getAbsolutePath());
		builder.append(File.separator);
		builder.append(id);
		builder.append("-");
		builder.append(MONTH_FORMATTER.format(date.getTime()));
		builder.append(".xml");
		
		return new File(builder.toString());
	}

	@Override
	public List<File> getDataFiles(Calendar startDate, Calendar endDate) {
		Calendar start = (Calendar) startDate.clone();
		start.set(Calendar.DAY_OF_MONTH, 1);

		Calendar end = (Calendar) endDate.clone();
		end.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH));

		List<File> result = new ArrayList<File>();
		while (start.compareTo(end) <= 0) {

			File f = getDataFile(start);
			if (f.exists()) {
				result.add(f);
			}

			start.add(Calendar.MONTH, 1);
		}
		return result;
	}

	@Override
	public EventListType read(File file) {
		try {
			if (file.exists()) {
				return JaxbUtil.unmarshal(EventListType.class, file);
			} else {
				return OBJECT_FACTORY.createEventListType();
			}
		} catch (JAXBException e) {
			return OBJECT_FACTORY.createEventListType();
		}
	}

	@Override
	public void write(EventListType doc, File f) {
		try {
			JaxbUtil.marshal(OBJECT_FACTORY.createEvents(doc), f);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public File getStorageLocation() {

		File f = new File(StoragePlugin.getDefault().getStoragePath().toOSString());
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

}
