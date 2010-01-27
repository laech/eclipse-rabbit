package rabbit.tracking.storage.xml;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.event.WorkbenchEvent;
import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.WorkbenchEventListType;
import rabbit.tracking.storage.xml.schema.WorkbenchEventType;

public class WorkbenchEventStorer<T extends WorkbenchEvent> extends AbstractXmlStorer<T, WorkbenchEventType, WorkbenchEventListType> {
	
	public WorkbenchEventStorer() {
	}

	@Override
	protected String getFileNamePrefix() {
		return "wbEvts";
	}

	@Override
	protected List<WorkbenchEventListType> getXmlTypeCategories(EventListType events) {
		return events.getWorkbenchEvents();
	}

	@Override
	protected boolean hasSameId(WorkbenchEventType x, T e) {
		
		boolean result = false;
		if (e.getPerspective() != null) {
			result = e.getPerspective().getId().equals(x.getPerspectiveId());
		}
		if (e.getWorkbenchPart() != null) {
			result = (result && e.getWorkbenchPart().getSite().getId().equals(x.getPartId()));
		}
		return result;
	}

	@Override
	protected boolean hasSameId(WorkbenchEventType x1, WorkbenchEventType x2) {
		
		return x1.getPartId().equals(x2.getPartId())
			&& x1.getPerspectiveId().equals(x2.getPerspectiveId());
	}

	@Override
	protected void merge(WorkbenchEventListType main, T e) {
		merge(main.getWorkbenchEvent(), e);
	}

	@Override
	protected void merge(WorkbenchEventListType main, WorkbenchEventListType data) {
		merge(main.getWorkbenchEvent(), data.getWorkbenchEvent());
	}

	@Override
	protected void merge(WorkbenchEventType main, T e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	protected void merge(WorkbenchEventType main, WorkbenchEventType x2) {
		main.setDuration(main.getDuration() + x2.getDuration());
	}

	@Override
	protected WorkbenchEventType newXmlType(T e) {
		
		WorkbenchEventType type = OBJECT_FACTORY.createWorkbenchEventType();
		type.setDuration(e.getDuration());
		type.setPartId(e.getWorkbenchPart().getSite().getId());
		type.setPerspectiveId(e.getPerspective().getId());
		
		return type;
	}

	@Override
	protected WorkbenchEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		
		WorkbenchEventListType type = OBJECT_FACTORY.createWorkbenchEventListType();
		type.setDate(date);
		
		return type;
	}

}
