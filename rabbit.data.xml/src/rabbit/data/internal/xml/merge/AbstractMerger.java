package rabbit.data.internal.xml.merge;

/**
 * TODO test, try testing with a type who's property returns null. Abstract
 * class containing default implementations for an {@link IMerger}.
 * 
 * @param <T> The object type to merge.
 */
public abstract class AbstractMerger<T> implements IMerger<T> {

  @Override
  public final boolean isMergeable(T t1, T t2) {
    if (t1 == null || t2 == null)
      throw new NullPointerException("Argument cannot be null");

    return doIsMergeable(t1, t2);
  }

  @Override
  public final T merge(T t1, T t2) {
    if (!isMergeable(t1, t2))
      throw new IllegalArgumentException("Objects are not mergeable");

    return doMerge(t1, t2);
  }

  /**
   * This method is called from {@link #isMergeable(Object, Object)} by the
   * super class after checking both arguments are not null. Subclasses can
   * safely compare the objects without further checking for null.
   * <p>
   * This method should not be invoked by subclasses.
   * </p>
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return True if the objects are mergeable, false otherwise.
   * 
   */
  protected abstract boolean doIsMergeable(T t1, T t2);

  /**
   * This method will be called from {@link #merge(Object, Object)} by the super
   * class after checking {@link #isMergeable(Object, Object)} return true on
   * the two objects, subclasses can safely merge the two objects without
   * further checking. If the two parameter objects are modifiable, they may be
   * modified during the operation. *
   * <p>
   * This method should not be invoked by subclasses.
   * </p>
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return An object as a result of the two objects merging.
   */
  protected abstract T doMerge(T t1, T t2);
}
