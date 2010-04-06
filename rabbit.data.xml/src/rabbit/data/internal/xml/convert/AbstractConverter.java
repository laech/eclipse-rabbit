package rabbit.data.internal.xml.convert;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

/**
 * Abstract {@link IConverter} class containing default implementations.
 * 
 * @param <F> The element to convert from.
 * @param <T> The element to convert to.
 */
public abstract class AbstractConverter<F, T> implements IConverter<F, T> {

  @Override
  public final T convert(F element) {
    checkNotNull(element, "Argument cannot be null");
    return doConvert(element);
  }

  /**
   * This method is called by {@link #convert(Object)} from the super class
   * after checking for null, subclasses can safely convert the element without
   * further checking.
   * 
   * @param element The element to convert from.
   * @return A converted element.
   */
  @Nonnull
  protected abstract T doConvert(@Nonnull F element);

}
