package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static rabbit.core.internal.storage.xml.AbstractStorer.isSameDate;
import static rabbit.core.internal.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.core.events.CommandEvent;
import rabbit.core.internal.storage.xml.CommandEventStorer;
import rabbit.core.internal.storage.xml.schema.events.CommandEventListType;
import rabbit.core.internal.storage.xml.schema.events.CommandEventType;

public class CommandEventStorerTest extends AbstractStorerTest<CommandEvent, CommandEventType, CommandEventListType> {

	private CommandEventStorer storer = create();

	/**
	 * Gets the workbench command service.
	 * 
	 * @return The command service.
	 */
	private ICommandService getCommandService() {
		return (ICommandService) PlatformUI.getWorkbench().getService(
				ICommandService.class);
	}

	@Override
	protected CommandEventStorer create() {
		return new CommandEventStorer();
	}

	private ExecutionEvent createExecutionEvent(String commandId) {
		return new ExecutionEvent(getCommandService()
				.getCommand(commandId), Collections.EMPTY_MAP, null, null);
	}

	@Override
	protected CommandEvent createEvent() {
		return new CommandEvent(Calendar.getInstance(), createExecutionEvent("adnk2o385"));
	}

	@Override
	protected CommandEvent createEvent2() {
		return new CommandEvent(Calendar.getInstance(), createExecutionEvent("23545656"));
	}

	@Override
	public void testCommit() {

		try {
			CommandEvent e = createEvent();
			storer.insert(e);
			storer.commit();
			assertTrue(dataFile.exists());

			List<CommandEventListType> allEvents = storer.getDataStore().read(dataFile).getCommandEvents();
			assertEquals(1, allEvents.size());

			CommandEventListType list = allEvents.get(0);
			assertEquals(1, list.getCommandEvent().size());

			CommandEventType event = list.getCommandEvent().get(0);
			assertEquals(e.getExecutionEvent().getCommand().getId(), event.getCommandId());
			assertEquals(1, event.getCount());

			assertTrue(getDataField(storer).isEmpty());

			// ...

			int totalCount = 2;
			e = createEvent();
			storer.insert(e);
			storer.commit();

			allEvents = storer.getDataStore().read(dataFile).getCommandEvents();
			assertEquals(1, allEvents.size());

			list = allEvents.get(0);
			assertEquals(1, list.getCommandEvent().size());

			event = list.getCommandEvent().get(0);
			assertEquals(totalCount, event.getCount());
			assertEquals(e.getExecutionEvent().getCommand().getId(), event.getCommandId());

			// ...

			// Insert an new and different event:

			CommandEvent eNew = createEvent();
			eNew.setExecutionEvent(createExecutionEvent("1334850426385"));
			storer.insert(eNew);
			storer.commit();

			allEvents = storer.getDataStore().read(dataFile).getCommandEvents();
			assertEquals(1, allEvents.size());

			list = allEvents.get(0);
			assertEquals(2, list.getCommandEvent().size());

			CommandEventType type = list.getCommandEvent().get(0);
			if (storer.hasSameId(event, type)) {
				type = list.getCommandEvent().get(1);
				event = list.getCommandEvent().get(0);
			} else {
				event = list.getCommandEvent().get(0);
			}

			assertEquals(eNew.getExecutionEvent().getCommand().getId(), type.getCommandId());
			assertEquals(1, type.getCount());

			assertEquals(e.getExecutionEvent().getCommand().getId(), event.getCommandId());
			assertEquals(totalCount, event.getCount());

			// ..

			e = createEvent();
			Calendar cal = e.getTime();
			int day = cal.get(Calendar.DAY_OF_MONTH);
			day = (day < 15) ? day + 1 : day - 1;
			cal.set(Calendar.DAY_OF_MONTH, day);
			storer.insert(e);
			storer.commit();

			allEvents = storer.getDataStore().read(dataFile).getCommandEvents();
			assertEquals(2, allEvents.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Override
	public void testHasSameId_typeAndEvent() {

		CommandEvent e = createEvent();
		CommandEventType type = objectFactory.createCommandEventType();
		type.setCommandId(e.getExecutionEvent().getCommand().getId());
		assertTrue(storer.hasSameId(type, e));

		type.setCommandId(type.getCommandId() + "abc");
		assertFalse(storer.hasSameId(type, e));
	}

	@Override
	public void testHasSameId_typeAndType() {

		CommandEvent e = createEvent();

		CommandEventType type = objectFactory.createCommandEventType();
		type.setCommandId(e.getExecutionEvent().getCommand().getId());

		CommandEventType type2 = objectFactory.createCommandEventType();
		type2.setCommandId(type.getCommandId());
		assertTrue(storer.hasSameId(type, type2));

		type.setCommandId(type.getCommandId() + "abc");
		assertFalse(storer.hasSameId(type, type2));
	}

	@Override
	public void testInsert() {
		try {
			Collection<CommandEventListType> data = getDataField(storer);

			assertEquals(0, data.size());

			// Insert a new event:

			CommandEvent e = createEvent();
			storer.insert(e);

			assertEquals(1, data.size());
			assertEquals(1, data.iterator().next().getCommandEvent().size());

			CommandEventType type = data.iterator().next().getCommandEvent().get(0);
			assertEquals(e.getExecutionEvent().getCommand().getId(), type.getCommandId());
			assertEquals(1, type.getCount());

			// Insert an event with the same partId and perspectiveId:

			long totalCount = 2;
			e = createEvent();
			storer.insert(e);

			assertEquals(1, data.size());
			assertEquals(1, data.iterator().next().getCommandEvent().size());
			assertTrue(isSameDate(e.getTime(), data.iterator().next().getDate()));

			type = data.iterator().next().getCommandEvent().get(0);
			assertEquals(e.getExecutionEvent().getCommand().getId(), type.getCommandId());
			assertEquals(totalCount, type.getCount());

			// Insert an new and different event:

			e = createEvent();
			e.setExecutionEvent(createExecutionEvent("nch1uhcbzysgnvc"));
			storer.insert(e);

			assertEquals(1, data.size());
			assertEquals(2, data.iterator().next().getCommandEvent().size());

			type = data.iterator().next().getCommandEvent().get(1);
			assertEquals(e.getExecutionEvent().getCommand().getId(), type.getCommandId());
			assertEquals(1, type.getCount());

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
			Collection<CommandEventListType> data = getDataField(storer);

			assertEquals(0, data.size());

			CommandEvent e = null;
			CommandEventType type = null;
			Collection<CommandEvent> list = new ArrayList<CommandEvent>();

			{// Insert a new event:
				e = createEvent();
				list.add(e);
				storer.insert(list);

				assertEquals(1, data.size());
				assertEquals(1, data.iterator().next().getCommandEvent().size());

				type = data.iterator().next().getCommandEvent().get(0);
				assertEquals(e.getExecutionEvent().getCommand().getId(), type.getCommandId());
				assertEquals(1, type.getCount());
			}

			{// Insert collection with two elements:
				// Make a new event with the same ids:
				int totalDuration = 2;
				CommandEvent eWithSameId = createEvent();

				// Make a new event with different ids:

				CommandEvent eNew = createEvent();
				eNew.setExecutionEvent(createExecutionEvent("cn874hdbi000283"));

				list.clear();
				list.add(eWithSameId);
				list.add(eNew);
				storer.insert(list);

				assertEquals(1, data.size());
				assertEquals(2, data.iterator().next().getCommandEvent().size());

				type = data.iterator().next().getCommandEvent().get(0);
				assertEquals(eWithSameId.getExecutionEvent().getCommand().getId(), type.getCommandId());
				assertEquals(totalDuration, type.getCount());

				type = data.iterator().next().getCommandEvent().get(1);
				assertEquals(eNew.getExecutionEvent().getCommand().getId(), type.getCommandId());
				assertEquals(1, type.getCount());
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

	@Override
	public void testMerge_listTypeAndEvent() {

		CommandEvent e = createEvent();
		CommandEventType main = storer.newXmlType(e);

		long totalCount = 2;
		storer.merge(main, e);
		assertEquals(totalCount, main.getCount());
	}

	@Override
	public void testMerge_listTypeAndlistType() {

		CommandEvent e = createEvent();
		CommandEventType x = storer.newXmlType(e);

		CommandEventListType main = objectFactory.createCommandEventListType();
		main.getCommandEvent().add(x);

		CommandEventListType tmp = objectFactory.createCommandEventListType();
		tmp.getCommandEvent().add(x);

		long totalCount = x.getCount() * 2;
		storer.merge(main, tmp);
		assertEquals(1, main.getCommandEvent().size());
		assertEquals(totalCount, main.getCommandEvent().get(0).getCount());
	}

	@Override
	public void testMerge_typeAndEvent() {

		CommandEvent e = createEvent();
		CommandEventType x = storer.newXmlType(e);
		storer.merge(x, e);
		assertEquals(2, x.getCount());
	}

	@Override
	public void testMerge_typeAndType() {

		String id = "id";
		int count = 102;

		CommandEventType x1 = objectFactory.createCommandEventType();
		x1.setCommandId(id);
		x1.setCount(count);

		CommandEventType x2 = objectFactory.createCommandEventType();
		x2.setCommandId(id);
		x2.setCount(count);

		storer.merge(x1, x2);
		assertEquals(count * 2, x1.getCount());
	}

	@Override
	public void testNewXmlTypeHolderXMLGregorianCalendar() {

		XMLGregorianCalendar cal = toXMLGregorianCalendarDate(Calendar.getInstance());
		CommandEventListType list = storer.newXmlTypeHolder(cal);
		assertEquals(cal, list.getDate());
	}

	@Override
	public void testNewXmlTypeT() {

		CommandEvent e = createEvent();
		CommandEventType type = storer.newXmlType(e);
		assertEquals(e.getExecutionEvent().getCommand().getId(), type.getCommandId());
		assertEquals(1, type.getCount());
	}

}
