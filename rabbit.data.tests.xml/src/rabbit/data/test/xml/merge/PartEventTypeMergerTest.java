package rabbit.data.test.xml.merge;

import rabbit.data.internal.xml.merge.PartEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PartEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see PartEventTypeMerger
 */
@SuppressWarnings("restriction")
public class PartEventTypeMergerTest extends AbstractMergerTest<PartEventType> {

  /**
   * Tests when {@link PartEventType#getPartId()} returns null on both
   * parameters,
   * {@link PartEventTypeMerger#isMergeable(PartEventType, PartEventType)}
   * should return true (because both null) instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetPartIdReturnsNull() {
    PartEventType t1 = new PartEventType();
    t1.setPartId(null);
    PartEventType t2 = new PartEventType();
    t2.setPartId(null);

    try {
      assertTrue(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return true instead of exception");
    }
  }

  /**
   * Tests when {@link PartEventType#getPartId()} returns null on the first
   * parameter, and returns not null on the second parameter,
   * {@link PartEventTypeeMerger#isMergeable(PartEventType, PartEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetPartIdReturnsNull() {
    PartEventType t1 = new PartEventType();
    t1.setPartId(null);
    PartEventType t2 = new PartEventType();
    t2.setPartId("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link PartEventType#getPartId()} returns null on the second
   * parameter, and returns not null on the first parameter,
   * {@link PartEventTypeMerger#isMergeable(PartEventType, PartEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetPartIdReturnsNull() {
    PartEventType t1 = new PartEventType();
    t1.setPartId("notNull");
    PartEventType t2 = new PartEventType();
    t2.setPartId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testIsMergeable() throws Exception {
    PartEventType t1 = createTargetType();
    PartEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setPartId(t1.getPartId());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    PartEventType t1 = createTargetType();
    t1.setDuration(100);

    PartEventType t2 = createTargetTypeDiff();
    t2.setPartId(t1.getPartId());
    t2.setDuration(3000);

    String partId = t1.getPartId();
    long totalDuration = t1.getDuration() + t2.getDuration();

    PartEventType result = merger.merge(t1, t2);
    assertEquals(partId, result.getPartId());
    assertEquals(totalDuration, result.getDuration());
  }

  @Override
  protected PartEventTypeMerger createMerger() {
    return new PartEventTypeMerger();
  }

  @Override
  protected PartEventType createTargetType() {
    PartEventType type = new PartEventType();
    type.setDuration(19834);
    type.setPartId("com.org.net.abc");
    return type;
  }

  @Override
  protected PartEventType createTargetTypeDiff() {
    PartEventType type = new PartEventType();
    type.setDuration(123);
    type.setPartId("apple.pear.orange");
    return type;
  }

}
