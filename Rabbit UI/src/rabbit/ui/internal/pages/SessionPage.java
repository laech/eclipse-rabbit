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
package rabbit.ui.internal.pages;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.TableLabelComparator;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public class SessionPage extends AbstractTableViewerPage {

	private IAccessor<Map<String, Long>> dataStore;
	private Map<String, Long> model;

	/** Constructs a new page. */
	public SessionPage() {
		dataStore = RabbitCore.getSessionDataAccessor();
		model = new LinkedHashMap<String, Long>();
	}

	@Override
	public long getValue(Object o) {
		Long value = model.get(o);
		return (value == null) ? 0 : value;
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxValue(0);
		model.clear();
		model = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (long value : model.values()) {
			if (value > getMaxValue()) {
				setMaxValue(value);
			}
		}
		getViewer().setInput(model.keySet());
	}

	@Override
	protected void createColumns(TableViewer viewer) {
		TableLabelComparator valueSorter = createValueSorterForTable(viewer);
		TableLabelComparator textSorter = new TableLabelComparator(viewer);

		int[] widths = new int[] { 200, 150 };
		int[] styles = new int[] { SWT.LEFT, SWT.RIGHT };
		String[] names = new String[] { "Date", "Duration" };
		for (int i = 0; i < names.length; i++) {
			TableColumn column = new TableColumn(viewer.getTable(), styles[i]);
			column.setText(names[i]);
			column.setWidth(widths[i]);
			column.addSelectionListener(
					(names.length - 1 == i) ? valueSorter : textSorter);
		}
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new CollectionContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new SessionPageLabelProvider(this);
	}
}
