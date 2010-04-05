package rabbit.data.test.xml.merge;

import rabbit.data.internal.xml.merge.CommandEventTypeMerger;
import rabbit.data.internal.xml.schema.events.CommandEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see CommandEventTypeMerger
 */
@SuppressWarnings("restriction")
public class CommandEventTypeMergerTest extends
    AbstractMergerTest<CommandEventType> {

  /**
   * Tests when {@link CommandEventType#getCommandId()} returns null on both
   * parameters,
   * {@link CommandEventTypeMerger#isMergeable(CommandEventType, CommandEventType)}
   * should return true (because both null) instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetCommandIdReturnsNull() {
    CommandEventType t1 = new CommandEventType();
    t1.setCommandId(null);
    CommandEventType t2 = new CommandEventType();
    t2.setCommandId(null);

    try {
      assertTrue(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return true instead of exception");
    }
  }

  /**
   * Tests when {@link CommandEventType#getCommandId()} returns null on the
   * first parameter, and returns not null on the second parameter,
   * {@link CommandEventTypeMerger#isMergeable(CommandEventType, CommandEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetCommandIdReturnsNull() {
    CommandEventType t1 = new CommandEventType();
    t1.setCommandId(null);
    CommandEventType t2 = new CommandEventType();
    t2.setCommandId("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link CommandEventType#getCommandId()} returns null on the
   * second parameter, and returns not null on the first parameter,
   * {@link CommandEventTypeMerger#isMergeable(CommandEventType, CommandEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetCommandIdReturnsNull() {
    CommandEventType t1 = new CommandEventType();
    t1.setCommandId("notNull");
    CommandEventType t2 = new CommandEventType();
    t2.setCommandId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testIsMergeable() throws Exception {
    CommandEventType t1 = createTargetType();
    CommandEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setCommandId(t1.getCommandId());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    CommandEventType t1 = createTargetType();
    t1.setCount(100);
    
    CommandEventType t2 = createTargetTypeDiff();
    t2.setCommandId(t1.getCommandId());
    t2.setCount(3000);

    String commandId = t1.getCommandId();
    int totalCount = t1.getCount() + t2.getCount();

    CommandEventType result = merger.merge(t1, t2);
    assertEquals(commandId, result.getCommandId());
    assertEquals(totalCount, result.getCount());
  }

  @Override
  protected CommandEventTypeMerger createMerger() {
    return new CommandEventTypeMerger();
  }

  @Override
  protected CommandEventType createTargetType() {
    CommandEventType type = new CommandEventType();
    type.setCommandId("commandIdA");
    type.setCount(1);
    return type;
  }

  @Override
  protected CommandEventType createTargetTypeDiff() {
    CommandEventType type = new CommandEventType();
    type.setCommandId("commandIdB");
    type.setCount(2);
    return type;
  }

}
