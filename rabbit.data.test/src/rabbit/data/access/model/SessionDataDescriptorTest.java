package rabbit.data.access.model;

import rabbit.data.access.model.SessionDataDescriptor;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * @see SessionDataDescriptor
 */
public class SessionDataDescriptorTest extends ValueDescriptorTest {

  @Override
  protected SessionDataDescriptor createDescriptor(LocalDate date, Duration value) {
    return new SessionDataDescriptor(date, value);
  }
}
