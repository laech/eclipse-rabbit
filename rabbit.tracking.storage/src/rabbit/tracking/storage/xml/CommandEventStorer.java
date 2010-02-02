package rabbit.tracking.storage.xml;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.event.CommandEvent;
import rabbit.tracking.storage.xml.schema.CommandEventListType;
import rabbit.tracking.storage.xml.schema.CommandEventType;
import rabbit.tracking.storage.xml.schema.EventListType;

public class CommandEventStorer extends
		AbstractXmlStorer<CommandEvent, CommandEventType, CommandEventListType> {

	/**
	 * Constructor.
	 */
	public CommandEventStorer() {
		super();
	}

	@Override
	protected CommandEventType newXmlType(CommandEvent e) {
		CommandEventType type = OBJECT_FACTORY.createCommandEventType();
		type.setCommandId(e.getExecutionEvent().getCommand().getId());
		type.setCount(1);
		return type;
	}

	@Override
	protected String getFileNamePrefix() {
		return "cmdEvts";
	}

	@Override
	protected boolean hasSameId(CommandEventType x, CommandEvent e) {

		return e.getExecutionEvent().getCommand().getId()
				.equals(x.getCommandId());
	}

	@Override
	protected void merge(CommandEventType x, CommandEvent e) {
		x.setCount(x.getCount() + 1);
	}
	
	@Override
	protected boolean hasSameId(CommandEventType x1, CommandEventType x2) {
		return x1.getCommandId().equals(x2.getCommandId());
	}
	
	@Override
	protected void merge(CommandEventType x1, CommandEventType x2) {
		x1.setCount(x1.getCount() + x2.getCount());
	}
	
	@Override
	protected void merge(CommandEventListType t1, CommandEventListType t2) {
		merge(t1.getCommandEvent(), t2.getCommandEvent());
	}

	@Override
	protected List<CommandEventListType> getXmlTypeCategories(EventListType events) {
		return events.getCommandEvents();
	}

	@Override
	protected void merge(CommandEventListType main, CommandEvent e) {
		merge(main.getCommandEvent(), e);
	}

	@Override
	protected CommandEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		CommandEventListType type = OBJECT_FACTORY.createCommandEventListType();
		type.setDate(date);
		return type;
	}
	
	public Map<String, Integer> getData(Calendar start, Calendar end) {

		Map<String, Integer> result = new HashMap<String, Integer>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);
		
		List<File> files = getDataFiles(start, end);
		for (File f : files) {
			for (CommandEventListType list : read(f).getCommandEvents()) {
				if (list.getDate().compare(startXmlCal) >= 0
						&& list.getDate().compare(endXmlCal) <= 0) {
					for (CommandEventType e : list.getCommandEvent()) {
						
						Integer count = result.get(e.getCommandId());
						if (count == null) {
							result.put(e.getCommandId(), e.getCount());
						} else {
							result.put(e.getCommandId(), e.getCount() + count);
						}
					}
				}
			}
		}
		for (Entry<String, Integer> entry : result.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println("\t" + entry.getValue());
		}
		return result;
	}
}
