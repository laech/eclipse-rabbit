package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.AbstractConverter;

import org.junit.Test;

/**
 * @see AbstractConverter
 */
@SuppressWarnings("restriction")
public abstract class AbstractConverterTest<F, T> {

  protected AbstractConverter<F, T> converter = createConverter();

  @Test(expected = NullPointerException.class)
  public void testConvert_paramNull() {
    converter.convert(null);
  }

  /**
   * Tests the converter returns what the subclass desires.
   */
  @Test
  public abstract void testConvert() throws Exception;

  /**
   * Creates a converter for testing.
   */
  protected abstract AbstractConverter<F, T> createConverter();

}
