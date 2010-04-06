package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.PerspectiveEventConverter;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

/**
 * @see PerspectiveEventConverter
 */
@SuppressWarnings("restriction")
public class PerspectiveEventConverterTest extends
    AbstractConverterTest<PerspectiveEvent, PerspectiveEventType> {

  @Override
  protected PerspectiveEventConverter createConverter() {
    return new PerspectiveEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    PerspectiveEvent event = new PerspectiveEvent(new DateTime(), 2,
        getPerspective());
    PerspectiveEventType type = converter.convert(event);
    assertEquals(event.getDuration(), type.getDuration());
    assertEquals(event.getPerspective().getId(), type.getPerspectiveId());
  }

  private IPerspectiveDescriptor getPerspective() {
    return PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[0];
  }

}
