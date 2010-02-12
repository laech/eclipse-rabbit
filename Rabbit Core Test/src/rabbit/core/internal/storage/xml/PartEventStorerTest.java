package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static rabbit.core.internal.storage.xml.AbstractStorer.OBJECT_FACTORY;

import java.util.Calendar;
import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import rabbit.core.events.PartEvent;
import rabbit.core.internal.storage.xml.PartEventStorer;
import rabbit.core.internal.storage.xml.schema.events.PartEventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventType;

public class PartEventStorerTest extends AbstractStorerTest2<PartEvent, PartEventType, PartEventListType> {

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

	@Override
	protected PartEvent createEvent2() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {

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

	@Override
	public void testHasSameId_typeAndEvent() {

		PartEventType x1 = OBJECT_FACTORY.createPartEventType();
		x1.setPartId("paId");

		PartEventType x2 = OBJECT_FACTORY.createPartEventType();
		x2.setPartId(x1.getPartId());

		assertTrue(storer.hasSameId(x1, x2));

		x2.setPartId("another");
		assertFalse(storer.hasSameId(x1, x2));
	}

	@Override
	public void testHasSameId_typeAndType() {

		PartEvent e = createEvent();

		PartEventType x = OBJECT_FACTORY.createPartEventType();
		x.setPartId(e.getWorkbenchPart().getSite().getId());

		assertTrue(storer.hasSameId(x, e));

		x.setPartId("");
		assertFalse(storer.hasSameId(x, e));
	}

	@Override
	protected PartEventStorer create() {
		return new PartEventStorer();
	}

	@Override
	protected List<PartEventType> getEventTypes(PartEventListType type) {
		return type.getPartEvent();
	}

	@Override
	protected boolean isEqual(PartEventType type, PartEvent event) {
		boolean isEqual = false;
		isEqual = type.getPartId().equals(event.getWorkbenchPart().getSite().getId());
		if (isEqual) {
			isEqual = (type.getDuration() == event.getDuration());
		}
		return isEqual;
	}

	@Override
	protected void mergeValue(PartEvent main, PartEvent tmp) {
		main.setDuration(main.getDuration() + tmp.getDuration());
	}

	@Override
	protected long getValue(PartEventType type) {
		return type.getDuration();
	}

	@Override
	protected long getValue(PartEvent event) {
		return event.getDuration();
	}
}
