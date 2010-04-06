package rabbit.data.internal.xml.convert;

import javax.annotation.Nonnull;

/**
 * A one way converter for converting from one object type to another.
 * 
 * @param <F> The object type to convert from.
 * @param <T> The object type to convert to.
 */
public interface IConverter<F, T> {

  /**
   * Converts the given element.
   * 
   * @param element The element to be converted.
   * @return A converted element.
   * @throws NullPointerException If element is null.
   */
  @Nonnull
  T convert(@Nonnull F element);

}
