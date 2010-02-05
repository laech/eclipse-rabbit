package rabbit.tracking.ui.internal;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// TODO
public class RabbitViewTest {

	private static Shell shell;

	// private RabbitView view;

	@BeforeClass public static void setUpBeforeClass() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				shell = new Shell(PlatformUI.getWorkbench().getDisplay());
			}
		});
	}

	@AfterClass public static void tearDownAfterClass() {
		shell.dispose();
	}

	@Before public void setUp() {
	// view = new RabbitView();
	}

	@Test public void testUpdateDate() {
		Calendar date = Calendar.getInstance();
		DateTime widget = new DateTime(shell, SWT.NONE);
		widget.setYear(1901);
		widget.setMonth(3);
		widget.setDay(9);
		RabbitView.updateDate(date, widget);
		assertEquals(widget.getYear(), date.get(Calendar.YEAR));
		assertEquals(widget.getMonth(), date.get(Calendar.MONTH));
		assertEquals(widget.getDay(), date.get(Calendar.DAY_OF_MONTH));
	}

	@Test public void testUpdateDateTime() {
		Calendar date = Calendar.getInstance();
		date.set(1999, 2, 3);
		DateTime widget = new DateTime(shell, SWT.NONE);
		RabbitView.updateDateTime(widget, date);
		assertEquals(date.get(Calendar.YEAR), widget.getYear());
		assertEquals(date.get(Calendar.MONTH), widget.getMonth());
		assertEquals(date.get(Calendar.DAY_OF_MONTH), widget.getDay());
	}
}
