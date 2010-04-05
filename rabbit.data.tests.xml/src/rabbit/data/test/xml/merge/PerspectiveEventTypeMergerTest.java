package rabbit.data.test.xml.merge;

import rabbit.data.internal.xml.merge.PerspectiveEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

@SuppressWarnings("restriction")
public class PerspectiveEventTypeMergerTest extends
    AbstractMergerTest<PerspectiveEventType> {

  /**
   * Tests when {@link PerspectiveEventType#getPerspectiveId()} returns null on
   * both parameters,
   * {@link PerspectiveEventTypeMerger#isMergeable(PerspectiveEventType, PerspectiveEventType)}
   * should return true (because both null) instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetPerspectiveIdReturnsNull() {
    PerspectiveEventType t1 = new PerspectiveEventType();
    t1.setPerspectiveId(null);
    PerspectiveEventType t2 = new PerspectiveEventType();
    t2.setPerspectiveId(null);

    try {
      assertTrue(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return true instead of exception");
    }
  }

  /**
   * Tests when {@link PerspectiveEventType#getPerspectiveId()} returns null on
   * the first parameter, and returns not null on the second parameter,
   * {@link PerspectiveEventTypeeMerger#isMergeable(PerspectiveEventType, PerspectiveEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetPerspectiveIdReturnsNull() {
    PerspectiveEventType t1 = new PerspectiveEventType();
    t1.setPerspectiveId(null);
    PerspectiveEventType t2 = new PerspectiveEventType();
    t2.setPerspectiveId("NotNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link PerspectiveEventType#getPerspectiveId()} returns null on
   * the second parameter, and returns not null on the first parameter,
   * {@link PerspectiveEventTypeMerger#isMergeable(PerspectiveEventType, PerspectiveEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetPerspectiveIdReturnsNull() {
    PerspectiveEventType t1 = new PerspectiveEventType();
    t1.setPerspectiveId("NotNull");
    PerspectiveEventType t2 = new PerspectiveEventType();
    t2.setPerspectiveId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  protected PerspectiveEventTypeMerger createMerger() {
    return new PerspectiveEventTypeMerger();
  }

  @Override
  protected PerspectiveEventType createTargetType() {
    PerspectiveEventType type = new PerspectiveEventType();
    type.setDuration(19834);
    type.setPerspectiveId("abc.1234");
    return type;
  }

  @Override
  protected PerspectiveEventType createTargetTypeDiff() {
    PerspectiveEventType type = new PerspectiveEventType();
    type.setDuration(98);
    type.setPerspectiveId("1234567890");
    return type;
  }

  @Override
  public void testIsMergeable() throws Exception {
    PerspectiveEventType t1 = createTargetType();
    PerspectiveEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setPerspectiveId(t1.getPerspectiveId());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    PerspectiveEventType t1 = createTargetType();
    t1.setDuration(100);

    PerspectiveEventType t2 = createTargetTypeDiff();
    t2.setPerspectiveId(t1.getPerspectiveId());
    t2.setDuration(3000);

    String perspectiveId = t1.getPerspectiveId();
    long totalDuration = t1.getDuration() + t2.getDuration();

    PerspectiveEventType result = merger.merge(t1, t2);
    assertEquals(perspectiveId, result.getPerspectiveId());
    assertEquals(totalDuration, result.getDuration());
  }

}
