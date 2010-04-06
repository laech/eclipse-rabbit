package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.FileEventConverter;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.store.model.FileEvent;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;

/**
 * @see FileEventConverter
 */
@SuppressWarnings("restriction")
public class FileEventConverterTest extends
    AbstractConverterTest<FileEvent, FileEventType> {

  @Override
  protected FileEventConverter createConverter() {
    return new FileEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    FileEvent event = new FileEvent(new DateTime(), 100, "file.a.c.b");
    FileEventType type = converter.convert(event);
    assertEquals(event.getFileId(), type.getFileId());
    assertEquals(event.getDuration(), type.getDuration());
  }

}
