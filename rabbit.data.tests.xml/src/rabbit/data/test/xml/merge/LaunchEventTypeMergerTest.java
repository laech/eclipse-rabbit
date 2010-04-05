package rabbit.data.test.xml.merge;

import rabbit.data.internal.xml.merge.LaunchEventTypeMerger;
import rabbit.data.internal.xml.schema.events.LaunchEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEventTypeMerger
 */
@SuppressWarnings("restriction")
public class LaunchEventTypeMergerTest extends
    AbstractMergerTest<LaunchEventType> {

  @Override
  public void testIsMergeable() throws Exception {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));

    t2 = createTargetTypeDiff();
    assertFalse(merger.isMergeable(t1, t2));

    // Launch mode ID:
    t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));
    t2.setLaunchModeId(t2.getLaunchModeId() + ".");
    assertFalse(merger.isMergeable(t1, t2));

    // Launch type ID:
    t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));
    t2.setLaunchTypeId(t2.getLaunchTypeId() + ".");
    assertFalse(merger.isMergeable(t1, t2));

    // Name:
    t2 = createTargetType();
    assertTrue(merger.isMergeable(t1, t2));
    t2.setName(t2.getName() + ".");
    assertFalse(merger.isMergeable(t1, t2));
  }

  @Test
  public void testIsMergeable_bothParamGetLaunchModeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchModeId(null);
    LaunchEventType t2 = createTargetType();
    t2.setLaunchModeId(null);

    try {
      assertTrue(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return true instead of exception");
    }
  }

  @Test
  public void testIsMergeable_bothParamGetLaunchTypeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchTypeId(null);
    LaunchEventType t2 = createTargetType();
    t2.setLaunchTypeId(null);

    try {
      assertTrue(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return true instead of exception");
    }
  }

  @Test
  public void testIsMergeable_bothParamGetNameReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setName(null);
    LaunchEventType t2 = createTargetType();
    t2.setName(null);

    try {
      assertTrue(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return true instead of exception");
    }
  }

  @Test
  public void testIsMergeable_firstParamGetLaunchModeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchModeId(null);
    LaunchEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_firstParamGetLaunchTypeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setLaunchTypeId(null);
    LaunchEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_firstParamGetNameReturnsNull() {
    LaunchEventType t1 = createTargetType();
    t1.setName(null);
    LaunchEventType t2 = createTargetType();

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_secondParamGetLaunchModeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    t2.setLaunchModeId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_secondParamGetLaunchTypeIdReturnsNull() {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    t2.setLaunchTypeId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Test
  public void testIsMergeable_secondParamGetNameReturnsNull() {
    LaunchEventType t1 = createTargetType();
    LaunchEventType t2 = createTargetType();
    t2.setName(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testMerge() throws Exception {
    LaunchEventType t1 = createTargetType();
    t1.setCount(10000);
    t1.setTotalDuration(98340);
    t1.getFileId().addAll(Arrays.asList(System.currentTimeMillis() + ""));

    LaunchEventType t2 = createTargetType();
    t2.setCount(10000);
    t2.setTotalDuration(98340);
    t2.getFileId().addAll(Arrays.asList(System.nanoTime() + ""));
    t2.getFileId().addAll(t1.getFileId());

    int totalCount = t1.getCount() + t2.getCount();
    long totalDuration = t1.getTotalDuration() + t2.getTotalDuration();
    Set<String> allFileIds = new HashSet<String>(t1.getFileId());
    allFileIds.addAll(t2.getFileId());

    LaunchEventType result = merger.merge(t1, t2);
    assertEquals(totalCount, result.getCount());
    assertEquals(totalDuration, result.getTotalDuration());
    assertEquals(allFileIds.size(), result.getFileId().size());
    assertTrue(allFileIds.containsAll(result.getFileId()));
  }

  @Override
  protected LaunchEventTypeMerger createMerger() {
    return new LaunchEventTypeMerger();
  }

  @Override
  protected LaunchEventType createTargetType() {
    LaunchEventType type = new LaunchEventType();
    type.setCount(1);
    type.setLaunchModeId("someModeId");
    type.setLaunchTypeId("someTypeId");
    type.setName("aName");
    type.setTotalDuration(1999);
    type.getFileId().add("1");
    type.getFileId().add("2");
    return type;
  }

  @Override
  protected LaunchEventType createTargetTypeDiff() {
    LaunchEventType type = new LaunchEventType();
    type.setCount(1111);
    type.setLaunchModeId("someModeId111111");
    type.setLaunchTypeId("someTypeId111111");
    type.setName("aName11111");
    type.setTotalDuration(1999111);
    type.getFileId().add("A");
    return type;
  }

}
