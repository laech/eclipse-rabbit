package rabbit.core.internal.storage.xml;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBException;

import rabbit.core.RabbitCore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

/**
 * Data stores.
 */
public enum DataStore implements IDataStore {

	COMMAND_STORE("commandEvents"), //$NON-NLS-1$
	PART_STORE("partEvents"), //$NON-NLS-1$
	PERSPECTIVE_STORE("perspectiveEvents"), //$NON-NLS-1$
	FILE_STORE("fileEvents"); //$NON-NLS-1$

	/**
	 * Formats a date into "yyyy-MM".
	 */
	private final DateFormat monthFormatter = new SimpleDateFormat("yyyy-MM"); //$NON-NLS-1$

	/**
	 * An object factory for creating XML object types.
	 */
	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	private String id;

	DataStore(String id) {
		this.id = id;
	}

	@Override
	public File getDataFile(Calendar date) {
		StringBuilder builder = new StringBuilder();
		builder.append(getStorageLocation().getAbsolutePath());
		builder.append(File.separator);
		builder.append(id);
		builder.append("-"); //$NON-NLS-1$
		builder.append(monthFormatter.format(date.getTime()));
		builder.append(".xml"); //$NON-NLS-1$
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
		File f = new File(RabbitCore.getDefault().getStoragePath().toOSString());
		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new RuntimeException();
			}
		}
		return f;
	}

}
