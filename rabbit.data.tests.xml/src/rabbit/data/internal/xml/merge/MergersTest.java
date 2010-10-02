package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.Mergers;
import rabbit.data.internal.xml.merge.PartEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PartEventType;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @see Mergers
 */
public class MergersTest {

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndElement_mergerNull() {
    Mergers.merge(null, Arrays.asList(""), "a");
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndElement_collectionNull() {
    Mergers.merge(new PartEventTypeMerger(), null, new PartEventType());
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndElement_elementNull() {
    Mergers.merge(new PartEventTypeMerger(), Collections
        .<PartEventType> emptyList(), null);
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndCollection_firstCollectionNull() {
    Mergers.merge(new PartEventTypeMerger(), null, Collections
        .<PartEventType> emptyList());
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndCollection_secondCollectionNull() {
    Mergers.merge(new PartEventTypeMerger(), Collections
        .<PartEventType> emptyList(), null);
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndCollection_mergerNull() {
    Mergers.merge(null, Collections.<String> emptyList(), Collections
        .<String> emptyList());
  }

  @Test
  public void testMerge_collectionAndElement_mergeableElements() {
    String id = "abc";
    PartEventType type1 = new PartEventType();
    type1.setDuration(11);
    type1.setPartId(id);
    PartEventType type2 = new PartEventType();
    type2.setDuration(9823);
    type2.setPartId(id);
    IMerger<PartEventType> merger = new PartEventTypeMerger();
    // Check the elements we just created are mergeable
    assertTrue(merger.isMergeable(type1, type2));

    List<PartEventType> collection = Lists.newArrayList(type1);
    Mergers.merge(merger, collection, type2);

    // Check the elements are merged:
    assertEquals(1, collection.size());
    PartEventType result = collection.get(0);
    assertEquals(id, result.getPartId());
    assertEquals(type1.getDuration() + type2.getDuration(), result
        .getDuration());
  }
  
  @Test
  public void testMerge_collectionAndElement_unmergeableElements() {
    String id1 = "13458";
    String id2 = "abcdef";
    long duration1 = 139834;
    long duration2 = 983471;
    PartEventType type1 = new PartEventType();
    PartEventType type2 = new PartEventType();
    type1.setDuration(duration1);
    type2.setDuration(duration2);
    type1.setPartId(id1);
    type2.setPartId(id2);
    
    IMerger<PartEventType> merger = new PartEventTypeMerger();
    // Check the elements we just created are not mergeable
    assertFalse(merger.isMergeable(type1, type2));

    List<PartEventType> collection = Lists.newArrayList(type1);
    Mergers.merge(merger, collection, type2);

    // Check the elements are merged:
    assertEquals(2, collection.size());
    assertSame(type1, collection.get(0));
    assertSame(type2, collection.get(1));
    assertEquals(id1, type1.getPartId());
    assertEquals(id2, type2.getPartId());
    assertEquals(duration1, type1.getDuration());
    assertEquals(duration2, type2.getDuration());
  }
}
