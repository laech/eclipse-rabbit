package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.PartEventConverter;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.store.model.PartEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

/**
 * @see PartEventConverter
 */
@SuppressWarnings("restriction")
public class PartEventConverterTest extends
    AbstractConverterTest<PartEvent, PartEventType> {

  @Override
  protected PartEventConverter createConverter() {
    return new PartEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    PartEvent event = new PartEvent(new DateTime(), 1001, getWorkbenchPart());
    PartEventType type = converter.convert(event);
    assertEquals(event.getDuration(), type.getDuration());
    assertEquals(event.getWorkbenchPart().getSite().getId(), type.getPartId());
  }

  private IWorkbenchPart getWorkbenchPart() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getPartService().getActivePart();
  }

}
