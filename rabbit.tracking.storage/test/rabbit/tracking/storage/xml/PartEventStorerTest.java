package rabbit.tracking.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.OBJECT_FACTORY;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.isSameDate;
import static rabbit.tracking.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.util.Calendar;
import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.event.PartEvent;
import rabbit.tracking.storage.xml.PartEventStorer;
import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

public class PartEventStorerTest extends AbstractXmlStorerTest2<PartEvent, PartEventType, PartEventListType> {

	private PartEvent event;

	private PartEventStorer storer = create();

	private IWorkbenchWindow win = getWorkbenchWindow();

	public IWorkbenchWindow getWorkbenchWindow() {

		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override public void run() {
				win = wb.getActiveWorkbenchWindow();
			}
		});
		return win;
	}

	@Override protected PartEvent createEvent() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override public void run() {
				try {
					IViewPart v = wb.getActiveWorkbenchWindow().getActivePage()
							.showView("org.eclipse.ui.views.TaskList");
					event = new PartEvent(Calendar.getInstance(), 10, v);
				} catch (PartInitException e) {
					e.printStackTrace();
					event = null;
				}
			}
		});
		return event;
	}

	@Override protected PartEvent createEvent2() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override public void run() {

				try {
					IViewPart v = wb.getActiveWorkbenchWindow().getActivePage()
							.showView("org.eclipse.ui.navigator.ProjectExplorer");
					event = new PartEvent(Calendar.getInstance(), 10, v);
				} catch (PartInitException e) {
					e.printStackTrace();
					event = null;
				}
			}
		});
		return event;
	}

	@Override public void testHasSameId_workbenchEventTypeAndEvent() {

		PartEventType x1 = OBJECT_FACTORY.createPartEventType();
		x1.setPartId("paId");

		PartEventType x2 = OBJECT_FACTORY.createPartEventType();
		x2.setPartId(x1.getPartId());

		assertTrue(storer.hasSameId(x1, x2));

		x2.setPartId("another");
		assertFalse(storer.hasSameId(x1, x2));
	}

	@Override public void testHasSameId_workbenchEventTypeAndWorkbenchEventType() {

		PartEvent e = createEvent();

		PartEventType x = OBJECT_FACTORY.createPartEventType();
		x.setPartId(e.getWorkbenchPart().getSite().getId());

		assertTrue(storer.hasSameId(x, e));

		x.setPartId("");
		assertFalse(storer.hasSameId(x, e));
	}

	@Override public void testMerge_xmlListTypeAndEvent() {

		PartEvent e = createEvent();
		PartEventType x = storer.newXmlType(e);

		PartEventListType main = OBJECT_FACTORY.createPartEventListType();
		main.getPartEvent().add(x);

		long totalDuration = x.getDuration() * 2;
		storer.merge(main, e);
		assertEquals(1, main.getPartEvent().size());
		assertEquals(totalDuration, main.getPartEvent().get(0).getDuration());
	}

	@Override public void testMerge_xmlListTypeAndxmlListType() {

		PartEvent e = createEvent();
		PartEventType x = storer.newXmlType(e);

		PartEventListType main = OBJECT_FACTORY.createPartEventListType();
		main.getPartEvent().add(x);

		PartEventListType tmp = OBJECT_FACTORY.createPartEventListType();
		tmp.getPartEvent().add(x);

		long totalDuration = x.getDuration() * 2;
		storer.merge(main, tmp);
		assertEquals(1, main.getPartEvent().size());
		assertEquals(totalDuration, main.getPartEvent().get(0).getDuration());
	}

	@Override public void testMerge_xmlTypeAndEvent() {

		PartEvent e = createEvent();
		PartEventType main = storer.newXmlType(e);

		long totalDuration = e.getDuration() + main.getDuration();
		storer.merge(main, e);
		assertEquals(totalDuration, main.getDuration());
	}

	@Override public void testMerge_xmlTypeAndXmlType() {

		PartEvent e = createEvent();
		PartEventType main = storer.newXmlType(e);
		PartEventType tmp = storer.newXmlType(e);

		long totalDuration = main.getDuration() + tmp.getDuration();
		storer.merge(main, tmp);
		assertEquals(totalDuration, main.getDuration());
	}

	@Override public void testNewXmlTypeT() {

		PartEvent e = createEvent();
		PartEventType xml = storer.newXmlType(e);

		assertEquals(xml.getPartId(), e.getWorkbenchPart().getSite().getId());
		assertEquals(xml.getDuration(), e.getDuration());
	}

	@Override public void testNewXmlTypeHolderXMLGregorianCalendar() {

		Calendar cal = Calendar.getInstance();
		PartEventListType type = storer.newXmlTypeHolder(toXMLGregorianCalendarDate(cal));
		assertTrue(isSameDate(cal, type.getDate()));
	}

	@Override protected PartEventStorer create() {
		return new PartEventStorer();
	}

	@Override protected List<PartEventType> getEventTypes(PartEventListType type) {
		return type.getPartEvent();
	}

	@Override protected boolean isEqual(PartEventType type, PartEvent event) {
		boolean isEqual = false;
		isEqual = type.getPartId().equals(event.getWorkbenchPart().getSite().getId());
		if (isEqual) {
			isEqual = (type.getDuration() == event.getDuration());
		}
		return isEqual;
	}

	@Override protected void mergeValue(PartEvent main, PartEvent tmp) {
		main.setDuration(main.getDuration() + tmp.getDuration());
	}
}
