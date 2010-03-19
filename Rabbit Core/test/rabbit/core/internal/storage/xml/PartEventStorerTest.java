/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import rabbit.core.events.PartEvent;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;
import rabbit.core.internal.storage.xml.schema.events.PartEventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventType;

public class PartEventStorerTest extends
		AbstractStorerTest2<PartEvent, PartEventType, PartEventListType> {

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
	public void testHasSameId_typeAndEvent() {

		PartEventType x1 = new ObjectFactory().createPartEventType();
		x1.setPartId("paId");

		PartEventType x2 = new ObjectFactory().createPartEventType();
		x2.setPartId(x1.getPartId());

		assertTrue(storer.hasSameId(x1, x2));

		x2.setPartId("another");
		assertFalse(storer.hasSameId(x1, x2));
	}

	@Override
	public void testHasSameId_typeAndType() {

		PartEvent e = createEvent();

		PartEventType x = new ObjectFactory().createPartEventType();
		x.setPartId(e.getWorkbenchPart().getSite().getId());

		assertTrue(storer.hasSameId(x, e));

		x.setPartId("");
		assertFalse(storer.hasSameId(x, e));
	}

	@Override
	protected PartEventStorer create() {
		return PartEventStorer.getInstance();
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
	protected List<PartEventType> getEventTypes(PartEventListType type) {
		return type.getPartEvent();
	}

	@Override
	protected long getValue(PartEvent event) {
		return event.getDuration();
	}

	@Override
	protected long getValue(PartEventType type) {
		return type.getDuration();
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
}
