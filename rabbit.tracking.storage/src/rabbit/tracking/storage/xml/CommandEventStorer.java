package rabbit.tracking.storage.xml;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.event.CommandEvent;
import rabbit.tracking.storage.xml.schema.CommandEventListType;
import rabbit.tracking.storage.xml.schema.CommandEventType;
import rabbit.tracking.storage.xml.schema.EventListType;

public class CommandEventStorer<T extends CommandEvent> extends AbstractXmlStorer<T, CommandEventType, CommandEventListType> {

	/**
	 * Constructor.
	 */
	public CommandEventStorer() {
		super();
	}

	@Override
	protected CommandEventType newXmlType(T e) {
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
	protected boolean hasSameId(CommandEventType x, T e) {

		return e.getExecutionEvent().getCommand().getId()
				.equals(x.getCommandId());
	}

	@Override
	protected void merge(CommandEventType x, T e) {
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
	protected void merge(CommandEventListType main, T e) {
		merge(main.getCommandEvent(), e);
	}

	@Override
	protected CommandEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		CommandEventListType type = OBJECT_FACTORY.createCommandEventListType();
		type.setDate(date);
		return type;
	}
}
