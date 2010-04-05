package rabbit.data.test.xml.merge;

import rabbit.data.internal.xml.merge.FileEventTypeMerger;
import rabbit.data.internal.xml.schema.events.FileEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see FileEventTypeMerger
 */
@SuppressWarnings("restriction")
public class FileEventTypeMergerTest extends AbstractMergerTest<FileEventType> {

  /**
   * Tests when {@link FileEventType#getFileId()} returns null on both
   * parameters,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return true (because both null) instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFileId(null);
    FileEventType t2 = new FileEventType();
    t2.setFileId(null);

    try {
      assertTrue(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return true instead of exception");
    }
  }

  /**
   * Tests when {@link FileEventType#getFileId()} returns null on the first
   * parameter, and returns not null on the second parameter,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFileId(null);
    FileEventType t2 = new FileEventType();
    t2.setFileId("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link FileEventType#getFileId()} returns null on the second
   * parameter, and returns not null on the first parameter,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFileId("notNull");
    FileEventType t2 = new FileEventType();
    t2.setFileId(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testIsMergeable() throws Exception {
    FileEventType t1 = createTargetType();
    FileEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setFileId(t1.getFileId());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    FileEventType t1 = createTargetType();
    t1.setDuration(100);

    FileEventType t2 = createTargetTypeDiff();
    t2.setFileId(t1.getFileId());
    t2.setDuration(3000);

    String fileId = t1.getFileId();
    long totalDuration = t1.getDuration() + t2.getDuration();

    FileEventType result = merger.merge(t1, t2);
    assertEquals(fileId, result.getFileId());
    assertEquals(totalDuration, result.getDuration());
  }

  @Override
  protected FileEventTypeMerger createMerger() {
    return new FileEventTypeMerger();
  }

  @Override
  protected FileEventType createTargetType() {
    FileEventType type = new FileEventType();
    type.setFileId("afileId");
    type.setDuration(10);
    return type;
  }

  @Override
  protected FileEventType createTargetTypeDiff() {
    FileEventType type = new FileEventType();
    type.setFileId("afileIdabcdefg123");
    type.setDuration(110);
    return type;
  }

}
