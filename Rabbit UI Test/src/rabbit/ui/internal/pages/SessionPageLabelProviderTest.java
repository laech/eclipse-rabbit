package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.storage.xml.SessionDataAccessor;
import rabbit.ui.internal.util.MillisConverter;

/**
 * Test for {@link SessionPageLabelProvider}
 */
public class SessionPageLabelProviderTest {

	private static Shell shell;
	private static SessionPage page;
	private static SessionPageLabelProvider provider;

	@BeforeClass
	public static void beforeClass() {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new SessionPage();
		page.createContents(shell);
		provider = new SessionPageLabelProvider(page);
	}

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	@Test
	public void testGetColumnTextImage() throws Exception {
		Format formatter = new SimpleDateFormat(SessionDataAccessor.DATE_FORMAT);
		String date = formatter.format(Calendar.getInstance().getTime());
		long value = 187598;
		Map<String, Long> data = SessionPageTest.getData(page);
		data.put(date, value);

		assertEquals(date, provider.getColumnText(date, 0));
		assertEquals(MillisConverter.toDefaultString(value), provider.getColumnText(date, 1));

		assertNull(provider.getColumnImage(date, 0));
		assertNull(provider.getColumnImage(date, 1));
	}
}
