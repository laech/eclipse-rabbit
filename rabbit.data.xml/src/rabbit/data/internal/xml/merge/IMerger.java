package rabbit.data.internal.xml.merge;

/**
 * An interface contains utility methods for merging data objects. These objects
 * usually consist of two "parts", one identify part and one value part, if the
 * identity parts are equal for two objects, then they are considered as
 * mergeable.
 * 
 * @param T The type of objects to merge.
 */
public interface IMerger<T> {

  /**
   * Checks whether the two objects are mergeable.
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return True if the objects are mergeable, false otherwise.
   * @throws NullPointerException If any of the arguments is null.
   */
  boolean isMergeable(T t1, T t2);

  /**
   * Merges the two objects into one. If the two parameter objects are
   * modifiable, they may be modified during the operation.
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return An object that is the result of merging the two parameter objects.
   * @throws IllegalArgumentException If {@link #isMergeable(Object, Object)}
   *           returns false on the two object.
   */
  T merge(T t1, T t2);
}
