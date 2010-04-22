package rabbit.ui.tests.viewers;

import rabbit.ui.internal.viewers.TreeNodes;

import com.google.common.base.Predicates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.jface.viewers.TreeNode;
import org.junit.Test;

/**
 * @see TreeNodes
 */
@SuppressWarnings("restriction")
public class TreeNodesTest {

  @Test
  public void testAppendToParent() {
    TreeNode root = new TreeNode(new Object());
    
    String value = "abc";
    TreeNode child = TreeNodes.appendToParent(root, value);
    assertEquals(1, root.getChildren().length);
    assertEquals(value, child.getValue());
  }
  
  @Test
  public void testFindChild() {
    TreeNode root = new TreeNode(new Object());
    TreeNodes.appendToParent(root, 1);
    TreeNodes.appendToParent(root, "");
    
    String value = "hello";
    TreeNode child = TreeNodes.appendToParent(root, value);
    assertNotNull(TreeNodes.findChild(root, value));
    assertEquals(child, TreeNodes.findChild(root, value));
  }
  
  @Test
  public void testFindChildRecursively() {
    String target = "abc";
    
    TreeNode root = new TreeNode(target);
    assertSame(root, TreeNodes.findChildRecursively(root, target));
    
    
    root.setChildren(new TreeNode[] { new TreeNode(""), new TreeNode("123") });
    assertSame(root, TreeNodes.findChildRecursively(root, target));
    
    
    root = new TreeNode(new Object());
    TreeNode targetNode = new TreeNode(target);
    root.setChildren(new TreeNode[] { new TreeNode(112), new TreeNode(12) });
    root.getChildren()[0].setChildren(new TreeNode[] { targetNode, new TreeNode("1") });
    assertSame(targetNode, TreeNodes.findChildRecursively(root, target));
    
    
    root = new TreeNode(new Object());
    root.setChildren(new TreeNode[] { new TreeNode(112), new TreeNode(12) });
    root.getChildren()[0].setChildren(new TreeNode[] { new TreeNode("1"), targetNode });
    assertSame(targetNode, TreeNodes.findChildRecursively(root, target));
    
    root = new TreeNode(target);
    root.setChildren(new TreeNode[] { new TreeNode(112), new TreeNode(12) });
    root.getChildren()[0].setChildren(new TreeNode[] { new TreeNode(2), new TreeNode("1") });
    assertNull(TreeNodes.findChildRecursively(root.getChildren()[0], target));
  }
  
  @Test
  public void testFindOrAppend() {
    TreeNode root = new TreeNode(new Object());
    
    String value = "world";
    assertNull(TreeNodes.findChild(root, value));
    assertEquals(value, TreeNodes.findOrAppend(root, value).getValue());
    assertEquals(1, root.getChildren().length);
  }
  
  @Test
  public void testFindMaxInt() {
    TreeNode root = new TreeNode(new Object());
    int max = 19834745;
    
    for (int i = 0; i < 100; i++)
      TreeNodes.appendToParent(root, String.valueOf(i));
    
    for (int i = 0; i < root.getChildren().length; i++)
      TreeNodes.appendToParent(root.getChildren()[i], max - i);
    
    assertEquals(max, TreeNodes.findMaxInt(root, Predicates.instanceOf(String.class)));
  }
  
  @Test
  public void testFindMaxLong() {
    TreeNode root = new TreeNode(new Object());
    long max = 99834745;
    
    for (int i = 0; i < 100; i++)
      TreeNodes.appendToParent(root, String.valueOf(i));
    
    for (int i = 0; i < root.getChildren().length; i++)
      TreeNodes.appendToParent(root.getChildren()[i], max - i);
    
    assertEquals(max, TreeNodes.findMaxLong(root, Predicates.instanceOf(String.class)));
  }
  
  @Test
  public void testIntValueOfSubtree() {
    TreeNode root = new TreeNode(new Object());
    int value = 0;
    
    for (int i = 0; i < 100; i++) {
      TreeNodes.appendToParent(root, i);
      value += i;
    }
    
    for (TreeNode child : root.getChildren()) {
      TreeNodes.appendToParent(child, value);
      value += value;
    }
    
    assertEquals(value, TreeNodes.intValueOfSubtree(root));
  }
  
  @Test
  public void testLongValueOfSubtree() {
    TreeNode root = new TreeNode(new Object());
    long value = 0;
    
    for (long i = 0; i < 100; i++) {
      TreeNodes.appendToParent(root, i);
      value += i;
    }
    
    for (TreeNode child : root.getChildren()) {
      TreeNodes.appendToParent(child, value);
      value += value;
    }
    
    assertEquals(value, TreeNodes.longValueOfSubtree(root));
  }
}
