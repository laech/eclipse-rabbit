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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.PerspectiveDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.TableLabelComparator;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

/**
 * A page displays perspective usage.
 */
public class PerspectivePage extends AbstractTableViewerPage {

	private IAccessor dataStore;
	private IPerspectiveRegistry registry;
	private Map<IPerspectiveDescriptor, Long> dataMapping;

	public PerspectivePage() {
		super();
		dataStore = new PerspectiveDataAccessor();
		dataMapping = new HashMap<IPerspectiveDescriptor, Long>();
		registry = PlatformUI.getWorkbench().getPerspectiveRegistry();
	}

	@Override
	public void createColumns(TableViewer viewer) {
		TableLabelComparator textSorter = new TableLabelComparator(viewer);
		TableLabelComparator valueSorter = createValueSorterForTable(viewer);

		int[] widths = new int[] { 150, 150 };
		int[] styles = new int[] { SWT.LEFT, SWT.RIGHT };
		String[] names = new String[] { "Name", "Usage" };
		for (int i = 0; i < names.length; i++) {
			TableColumn column = new TableColumn(viewer.getTable(), styles[i]);
			column.setText(names[i]);
			column.setWidth(widths[i]);
			column.addSelectionListener(
					(names.length - 1 == i) ? valueSorter : textSorter);
		}
	}

	@Override
	public long getValue(Object o) {
		Long value = dataMapping.get(o);
		return (value == null) ? 0 : value;
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxValue(0);
		dataMapping.clear();

		Map<String, Long> map = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Map.Entry<String, Long> entry : map.entrySet()) {
			IPerspectiveDescriptor pd = registry.findPerspectiveWithId(entry.getKey());
			if (pd == null) {
				pd = new UndefinedPerspectiveDescriptor(entry.getKey());
			}
			dataMapping.put(pd, entry.getValue());
			if (entry.getValue() > getMaxValue()) {
				setMaxValue(entry.getValue());
			}
		}
		getViewer().setInput(dataMapping.keySet());
	}

	@Override
	protected IContentProvider createContentProvider() {
		return new CollectionContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new PerspectivePageLabelProvider(this);
	}

}
