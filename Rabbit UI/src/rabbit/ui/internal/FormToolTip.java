package rabbit.ui.internal;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import rabbit.ui.internal.util.PageDescriptor;

/**
 * A form tool tip.
 */
public class FormToolTip extends ToolTip {

	private FormToolkit toolkit;
	private PageDescriptor client;

	/**
	 * Constructor.
	 * 
	 * @param control
	 *            The control.
	 * @param kit
	 *            The <tt>FormToolkit</tt> to draw the tool tip.
	 * @param page
	 *            The client containing the information.
	 */
	public FormToolTip(Control control, FormToolkit kit, PageDescriptor page) {
		super(control);
		toolkit = kit;
		client = page;
	}

	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		Form form = toolkit.createForm(parent);
		form.setMessage(client.getName(), IMessageProvider.INFORMATION);
		form.getBody().setLayout(new GridLayout());
		toolkit.createLabel(form.getBody(), client.getDescription());
		toolkit.decorateFormHeading(form);
		return form;
	}
}
