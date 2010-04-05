package rabbit.data.test.xml.merge;

import rabbit.data.internal.xml.merge.AbstractMerger;

import org.junit.Test;

/**
 * @see AbstractMerger
 */
@SuppressWarnings("restriction")
public abstract class AbstractMergerTest<T> {

  protected AbstractMerger<T> merger = createMerger();

  /**
   * Tests {@link AbstractMerger#isMergeable(Object, Object)} returns what's
   * desire for the subclass implementation.
   */
  @Test
  public abstract void testIsMergeable() throws Exception;

  @Test(expected = NullPointerException.class)
  public void testIsMergeable_bothParamNull() {
    merger.isMergeable(null, null);
  }

  @Test(expected = NullPointerException.class)
  public void testIsMergeable_firstParamNull() {
    merger.isMergeable(null, createTargetType());
  }

  @Test(expected = NullPointerException.class)
  public void testIsMergeable_secondParamNull() {
    merger.isMergeable(createTargetType(), null);
  }

  /**
   * Tests {@link AbstractMerger#merge(Object, Object)} does what the subclass
   * implementation desires.
   */
  @Test
  public abstract void testMerge() throws Exception;

  @Test(expected = NullPointerException.class)
  public void testMerge_bothParamNull() {
    merger.merge(null, null);
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_firstParamNull() {
    merger.merge(null, createTargetType());
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_secondParamNull() {
    merger.merge(createTargetType(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMerger_withNotMergeableParam() {
    merger.merge(createTargetType(), createTargetTypeDiff());
  }

  /**
   * Creates a merger for testing.
   */
  protected abstract AbstractMerger<T> createMerger();

  /**
   * Creates a target type for testing, all fields must be filled. Subsequence
   * calls to this method must return equals objects.
   */
  protected abstract T createTargetType();

  /**
   * Creates a target type for testing, all fields must be filled, and is
   * different to {@link #createTargetType()}, i.e. calling {@link
   * AbstractMerger#isMergeable(createTargetType(), createTargetType2())} will
   * always return false. Subsequence calls to this method must return equals
   * objects.
   */
  protected abstract T createTargetTypeDiff();
}
