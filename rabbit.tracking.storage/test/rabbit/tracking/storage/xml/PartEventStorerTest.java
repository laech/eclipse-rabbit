package rabbit.tracking.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.OBJECT_FACTORY;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.isSameDate;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.toXMLGregorianCalendarDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.event.PartEvent;
import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

public class PartEventStorerTest extends AbstractXmlStorerTest<PartEvent, PartEventType, PartEventListType> {

	private PartEvent event;
	
	private PartEventStorer storer = create();
	
	private IWorkbenchWindow win = getWorkbenchWindow(); 
	
	public IWorkbenchWindow getWorkbenchWindow() {
		
		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				win = wb.getActiveWorkbenchWindow();
			}
		});
		return win;
	}
	
	@Override
	protected PartEvent createEvent() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				
				event = new PartEvent(Calendar.getInstance(), 10, 
						wb.getActiveWorkbenchWindow().getPartService().getActivePart());
			}			
		});
		return event;
	}
	
	@Override
	public void testHasSameId_workbenchEventTypeAndEvent() {
		
		PartEventType x1 = OBJECT_FACTORY.createPartEventType();
		x1.setPartId("paId");
		
		PartEventType x2 = OBJECT_FACTORY.createPartEventType();
		x2.setPartId(x1.getPartId());
		
		assertTrue(storer.hasSameId(x1, x2));
		
		x2.setPartId("another");
		assertFalse(storer.hasSameId(x1, x2));
	}

	@Override
	public void testHasSameId_workbenchEventTypeAndWorkbenchEventType() {

		PartEvent e = createEvent();

		PartEventType x = OBJECT_FACTORY.createPartEventType();
		x.setPartId(e.getWorkbenchPart().getSite().getId());

		assertTrue(storer.hasSameId(x, e));

		x.setPartId("");
		assertFalse(storer.hasSameId(x, e));
	}

	@Override
	public void testMerge_xmlListTypeAndEvent() {
		
		PartEvent e = createEvent();
		PartEventType x = storer.newXmlType(e);
		
		PartEventListType main = OBJECT_FACTORY.createPartEventListType();
		main.getPartEvent().add(x);
		
		long totalDuration = x.getDuration() * 2;
		storer.merge(main, e);
		assertEquals(1, main.getPartEvent().size());
		assertEquals(totalDuration, main.getPartEvent().get(0).getDuration());
	}

	@Override
	public void testMerge_xmlListTypeAndxmlListType() {
		
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

	@Override
	public void testMerge_xmlTypeAndEvent() {
		
		PartEvent e = createEvent();
		PartEventType main = storer.newXmlType(e);
		
		long totalDuration = e.getDuration() + main.getDuration();
		storer.merge(main, e);
		assertEquals(totalDuration, main.getDuration());
	}

	@Override
	public void testMerge_xmlTypeAndXmlType() {
		
		PartEvent e = createEvent();
		PartEventType main = storer.newXmlType(e);
		PartEventType tmp = storer.newXmlType(e);
		
		long totalDuration = main.getDuration() + tmp.getDuration();
		storer.merge(main, tmp);
		assertEquals(totalDuration, main.getDuration());
	}

	@Override
	public void testNewXmlTypeT() {
		
		PartEvent e = createEvent();
		PartEventType xml = storer.newXmlType(e);
		
		assertEquals(xml.getPartId(), e.getWorkbenchPart().getSite().getId());
		assertEquals(xml.getDuration(), e.getDuration());
	}

	@Override
	public void testNewXmlTypeHolderXMLGregorianCalendar() {
		
		Calendar cal = Calendar.getInstance();
		PartEventListType type = storer.newXmlTypeHolder(toXMLGregorianCalendarDate(cal));
		assertTrue(isSameDate(cal, type.getDate()));
	}

	@Override
	protected PartEventStorer create() {
		return new PartEventStorer();
	}

	@Override
	public void testCommit() {
		
		try {
			PartEvent e = createEvent();
			storer.insert(e);
			storer.commit();
			assertTrue(dataFile.exists());

			List<PartEventListType> allEvents = storer.getDataStore().read(dataFile).getPartEvents();
			assertEquals(1, allEvents.size());

			PartEventListType list = allEvents.get(0);
			assertEquals(1, list.getPartEvent().size());

			PartEventType event = list.getPartEvent().get(0);
			assertEquals(e.getDuration(), event.getDuration());
			assertEquals(e.getWorkbenchPart().getSite().getId(), event.getPartId());

			assertTrue(getDataField(storer).isEmpty());

			//...

			long totalDuration = e.getDuration();
			e = createEvent();
			e.setDuration(101);
			totalDuration += e.getDuration();
			storer.insert(e);
			storer.commit();

			allEvents = storer.getDataStore().read(dataFile).getPartEvents();
			assertEquals(1, allEvents.size());

			list = allEvents.get(0);
			assertEquals(1, list.getPartEvent().size());

			event = list.getPartEvent().get(0);
			assertEquals(totalDuration, event.getDuration());
			assertEquals(e.getWorkbenchPart().getSite().getId(), event.getPartId());
			
			//...
			
			// Insert an new and different event:
			
			String partId = "org.eclipse.ui.views.ProblemView";
			// Make sure the new part is not the old one.
			assertFalse("View already opened, please choose another one.", 
					partId.equals(e.getWorkbenchPart().getSite().getId()));
			// Then open it.
			IWorkbenchPart newPart = getWorkbenchWindow().getActivePage().showView(partId);

			PartEvent e2 = createEvent();
			e2.setWorkbenchPart(newPart);
			storer.insert(e2);
			storer.commit();
			
			allEvents = storer.getDataStore().read(dataFile).getPartEvents();
			assertEquals(1, allEvents.size());

			list = allEvents.get(0);
			assertEquals(2, list.getPartEvent().size());

			PartEventType type = list.getPartEvent().get(0);
			if (storer.hasSameId(event, type)) {
				type = list.getPartEvent().get(1);
				event = list.getPartEvent().get(0);
			} else {
				event = list.getPartEvent().get(0);
			}
			
			assertEquals(e2.getWorkbenchPart().getSite().getId(), type.getPartId());
			assertEquals(e2.getDuration(), type.getDuration());
			
			assertEquals(e.getWorkbenchPart().getSite().getId(), event.getPartId());
			assertEquals(totalDuration, event.getDuration());
			
			//..

			e = createEvent();
			Calendar cal = e.getTime();
			int day = cal.get(Calendar.DAY_OF_MONTH);
			day = (day < 15) ? day + 1 : day - 1;
			cal.set(Calendar.DAY_OF_MONTH, day);
			storer.insert(e);
			storer.commit();
			
			allEvents = storer.getDataStore().read(dataFile).getPartEvents();
			assertEquals(2, allEvents.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Override
	public void testInsert() {
		
		try {
			Collection<PartEventListType> data = getDataField(storer);
			
			assertEquals(0, data.size());
			
			// Insert a new event:
			
			PartEvent e = createEvent();
			storer.insert(e);
			
			assertEquals(1, data.size());
			assertEquals(1, data.iterator().next().getPartEvent().size());
			
			PartEventType type = data.iterator().next().getPartEvent().get(0);
			assertEquals(e.getWorkbenchPart().getSite().getId(), type.getPartId());
			assertEquals(e.getDuration(), type.getDuration());
			
			// Insert an event with the same partId and perspectiveId:
			
			long totalDuration = e.getDuration();
			e = createEvent();
			totalDuration += e.getDuration();
			storer.insert(e);
			
			assertEquals(1, data.size());
			assertEquals(1, data.iterator().next().getPartEvent().size());
			
			type = data.iterator().next().getPartEvent().get(0);
			assertEquals(e.getWorkbenchPart().getSite().getId(), type.getPartId());
			assertEquals(totalDuration, type.getDuration());
			
			// Insert an new and different event:
			
			String partId = "org.eclipse.ui.views.TaskList";
			// Make sure the new part is not the old one.
			assertFalse("View already opened, please choose another one.", 
					partId.equals(e.getWorkbenchPart().getSite().getId()));
			// Then open it.
			IWorkbenchPart newPart = getWorkbenchWindow().getActivePage().showView(partId);

			e = createEvent();
			e.setWorkbenchPart(newPart);
			storer.insert(e);
			
			assertEquals(1, data.size());
			assertEquals(2, data.iterator().next().getPartEvent().size());
			
			type = data.iterator().next().getPartEvent().get(1);
			assertEquals(e.getWorkbenchPart().getSite().getId(), type.getPartId());
			assertEquals(e.getDuration(), type.getDuration());
			

			e = createEvent();
			Calendar cal = e.getTime();
			int day = cal.get(Calendar.DAY_OF_MONTH);
			day = (day < 15) ? day + 1 : day - 1;
			cal.set(Calendar.DAY_OF_MONTH, day);

			storer.insert(e);

			assertEquals(2, data.size());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Override
	public void testInsertCollection() {
		try {
			Collection<PartEventListType> data = getDataField(storer);
			
			assertEquals(0, data.size());
			
			PartEvent e = null;
			PartEventType type = null;
			List<PartEvent> list = new ArrayList<PartEvent>();
			
			{// Insert a new event:
				e = createEvent();
				list.add(e);
				storer.insert(list);

				assertEquals(1, data.size());
				assertEquals(1, data.iterator().next().getPartEvent().size());

				type = data.iterator().next().getPartEvent().get(0);
				assertEquals(e.getWorkbenchPart().getSite().getId(), type.getPartId());
				assertEquals(e.getDuration(), type.getDuration());
			}
			
			{// Insert collection with two elements:
				// Make a new event with the same ids:
				long totalDuration = e.getDuration();
				PartEvent eWithSameId = createEvent();
				totalDuration += eWithSameId.getDuration();


				// Make a new event with different ids:

				String partId = "org.eclipse.jdt.ui.PackageExplorer";
				// Make sure the new part is not the old one.
				assertFalse("View already opened, please choose another one.", 
						partId.equals(e.getWorkbenchPart().getSite().getId()));
				// Then open it.
				IWorkbenchPart newPart = getWorkbenchWindow().getActivePage().showView(partId);

				PartEvent eNew = createEvent();
				eNew.setWorkbenchPart(newPart);

				list.clear();
				list.add(eWithSameId);
				list.add(eNew);
				storer.insert(list);

				assertEquals(1, data.size());
				assertEquals(2, data.iterator().next().getPartEvent().size());

				type = data.iterator().next().getPartEvent().get(0);
				assertEquals(eWithSameId.getWorkbenchPart().getSite().getId(), type.getPartId());
				assertEquals(totalDuration, type.getDuration());

				type = data.iterator().next().getPartEvent().get(1);
				assertEquals(eNew.getWorkbenchPart().getSite().getId(), type.getPartId());
				assertEquals(eNew.getDuration(), type.getDuration());
			}
			
			{// Insert event of a different date:
				list.clear();
				e = createEvent();
				Calendar cal = e.getTime();
				int day = cal.get(Calendar.DAY_OF_MONTH);
				day = (day < 15) ? day + 1 : day - 1;
				cal.set(Calendar.DAY_OF_MONTH, day);
				
				list.add(e);
				storer.insert(list);
				
				assertEquals(2, data.size());
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
}
