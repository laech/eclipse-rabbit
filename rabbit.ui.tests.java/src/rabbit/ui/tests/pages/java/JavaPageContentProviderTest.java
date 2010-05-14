package rabbit.ui.tests.pages.java;

import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.ui.internal.pages.java.JavaCategory;
import rabbit.ui.internal.pages.java.JavaPage;
import rabbit.ui.internal.pages.java.JavaPageContentProvider;
import rabbit.ui.internal.util.ICategory;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * @see JavaPageContentProvider
 */
@SuppressWarnings("restriction")
public class JavaPageContentProviderTest {
  
  private static Shell shell;
  private static JavaPage page;
  private static JavaPageContentProvider provider;

  private static IJavaProject project;
  private static IPackageFragmentRoot packageRoot;
  private static IPackageFragment packageFragment;
  private static ITypeRoot typeRoot;
  private static IType type;
  private static IMethod method;
  
  private static final String METHOD_ID = "=Enfo/src<enfo{EnfoPlugin.java[EnfoPlugin~getDefault";

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new JavaPage();
    page.createContents(shell);
    provider = new JavaPageContentProvider(page.getViewer());
    page.getViewer().setContentProvider(provider);
    
    method = (IMethod) JavaCore.create(METHOD_ID);
    type = method.getDeclaringType();
    typeRoot = type.getTypeRoot();
    packageFragment = type.getPackageFragment();
    packageRoot = (IPackageFragmentRoot) packageFragment.getParent();
    project = packageRoot.getJavaProject();
  }
  
  @Test
  public void testCategories() {
    Set<ICategory> selected = Sets.newHashSet(provider.getSelectedCategories());
    Set<ICategory> unselected = Sets.newHashSet(provider.getUnselectedCategories());
    assertEquals(0, Sets.intersection(selected, unselected).size());
    
    Set<ICategory> all = Sets.union(selected, unselected);
    Set<JavaCategory> set = Sets.newHashSet(JavaCategory.values());
    assertEquals(set.size(), all.size());
    assertEquals(0, Sets.difference(all, set).size());
  }

  @Test
  public void testHasChildren() throws Exception {
    JavaDataDescriptor des = new JavaDataDescriptor(
        new LocalDate(), 1009823, METHOD_ID);
    page.getViewer().setInput(Arrays.asList(des));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(JavaCategory.PROJECT);
    assertFalse(provider.hasChildren(root.getChildren()[0]));

    provider.setSelectedCategories(JavaCategory.PROJECT, JavaCategory.DATE);
    assertTrue(provider.hasChildren(root.getChildren()[0]));
    assertFalse(provider.hasChildren(root.getChildren()[0].getChildren()[0]));
  }

  @Test
  public void testGetChildren() throws Exception {
    // Two data descriptor of different dates, same element, different value:
    JavaDataDescriptor d1 = new JavaDataDescriptor(
        new LocalDate(), 1009823, METHOD_ID);
    JavaDataDescriptor d2 = new JavaDataDescriptor(
        d1.getDate().minusDays(1), 123, METHOD_ID);

    page.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    // Set the data to categorize by package, then by dates:
    provider.setSelectedCategories(JavaCategory.PACKAGE, JavaCategory.DATE);
    assertEquals(1, root.getChildren().length);
    TreeNode fileNode = root.getChildren()[0];
    assertTrue(fileNode.getValue() instanceof IPackageFragment);

    TreeNode[] dateNodes = (TreeNode[]) provider.getChildren(fileNode);
    assertEquals(2, dateNodes.length);
    assertTrue(dateNodes[0].getValue() instanceof LocalDate);
    assertTrue(dateNodes[1].getValue() instanceof LocalDate);
    Set<Object> set = Sets.newHashSet(dateNodes[0].getValue(), dateNodes[1].getValue());
    assertTrue(set.contains(d1.getDate()));
    assertTrue(set.contains(d2.getDate()));
  }

  @Test
  public void testGetElement() throws Exception {
    // Two data descriptor of different dates, same element, different value:
    JavaDataDescriptor d1 = new JavaDataDescriptor(
        new LocalDate(), 1009823, METHOD_ID);
    JavaDataDescriptor d2 = new JavaDataDescriptor(
        d1.getDate().minusDays(1), 123, METHOD_ID);

    page.getViewer().setInput(Arrays.asList(d1, d2));

    provider.setSelectedCategories(JavaCategory.DATE);
    // Passing null is OK, the provider should return the children of its "root"
    // Size is two, because we defined two data descriptors of different dates:
    assertEquals(2, provider.getElements(null).length);
    TreeNode[] nodes = (TreeNode[]) provider.getElements(null);
    assertTrue(nodes[0].getValue() instanceof LocalDate);
    assertTrue(nodes[1].getValue() instanceof LocalDate);
    Set<LocalDate> dates = Sets.newTreeSet();
    dates.add((LocalDate) nodes[0].getValue());
    dates.add((LocalDate) nodes[1].getValue());
    assertTrue(dates.contains(d1.getDate()));
    assertTrue(dates.contains(d2.getDate()));

    provider.setSelectedCategories(JavaCategory.TYPE);
    assertEquals(1, provider.getElements(null).length);
    assertEquals(new TreeNode(type), provider.getElements(null)[0]);
  }

  @Test
  public void testGetMaxValue() {
    // Two data descriptor of different dates, same element, different value:
    JavaDataDescriptor d1 = new JavaDataDescriptor(
        new LocalDate(), 1009823, METHOD_ID);
    JavaDataDescriptor d2 = new JavaDataDescriptor(
        d1.getDate().minusDays(1), 123, METHOD_ID);

    // Date
    page.getViewer().setInput(Arrays.asList(d1, d2));
    provider.setSelectedCategories(JavaCategory.DATE);
    provider.setPaintCategory(JavaCategory.DATE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    // TYPE
    // Set to JavaCategory.TYPE so that the two data descriptors representing the
    // same file will be merged as a single tree node:
    provider.setSelectedCategories(JavaCategory.TYPE);
    provider.setPaintCategory(JavaCategory.TYPE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    // Separate the data descriptors by dates:
    provider.setSelectedCategories(JavaCategory.DATE, JavaCategory.TYPE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    // TYPE_ROOT
    provider.setSelectedCategories(JavaCategory.TYPE_ROOT);
    provider.setPaintCategory(JavaCategory.TYPE_ROOT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    provider.setSelectedCategories(JavaCategory.DATE, JavaCategory.TYPE_ROOT);
    assertEquals(d1.getValue(), provider.getMaxValue());

    // Project
    provider.setSelectedCategories(JavaCategory.PROJECT);
    provider.setPaintCategory(JavaCategory.PROJECT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    provider.setSelectedCategories(JavaCategory.DATE, JavaCategory.PROJECT);
    assertEquals(d1.getValue(), provider.getMaxValue());
    
    // PACKAGE
    provider.setSelectedCategories(JavaCategory.PACKAGE);
    provider.setPaintCategory(JavaCategory.PACKAGE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    
    // PACKAGE_ROOT
    provider.setSelectedCategories(JavaCategory.PACKAGE_ROOT);
    provider.setPaintCategory(JavaCategory.PACKAGE_ROOT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    
    // METHOD
    provider.setSelectedCategories(JavaCategory.METHOD);
    provider.setPaintCategory(JavaCategory.METHOD);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
  }

  @Test
  public void testGetSelectedCategories() {
    assertNotNull(provider.getSelectedCategories());
    // Should never be empty, if set to empty or null, defaults should be used:
    assertFalse(provider.getSelectedCategories().length == 0);
    ICategory[] categories = new ICategory[] { JavaCategory.DATE, JavaCategory.TYPE };
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());

    categories = new ICategory[] { JavaCategory.PROJECT, JavaCategory.PACKAGE };
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());
  }

  @Test
  public void testGetUnselectedCategories() {
    Set<JavaCategory> all = Sets.newHashSet(JavaCategory.values());
    ICategory[] categories = all.toArray(new ICategory[all.size()]);
    provider.setSelectedCategories(categories);
    assertEquals(0, provider.getUnselectedCategories().length);

    categories = new ICategory[] { JavaCategory.DATE, JavaCategory.TYPE };
    provider.setSelectedCategories(categories);

    Set<JavaCategory> unselect = Sets.difference(all, Sets.newHashSet(categories));
    assertEquals(unselect.size(), provider.getUnselectedCategories().length);
    assertTrue(unselect.containsAll(Arrays.asList(provider
        .getUnselectedCategories())));
  }

  @Test
  public void testGetValue() throws Exception {
    // Two data descriptor of different dates, same element, different value:
    JavaDataDescriptor d1 = new JavaDataDescriptor(
        new LocalDate(), 1009823, METHOD_ID);
    JavaDataDescriptor d2 = new JavaDataDescriptor(
        d1.getDate().minusDays(1), 123, METHOD_ID);

    page.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(JavaCategory.TYPE);
    TreeNode fileNode = root.getChildren()[0];
    assertEquals(d1.getValue() + d2.getValue(), provider.getValue(fileNode));

    provider.setSelectedCategories(JavaCategory.DATE);
    TreeNode[] dateNodes = root.getChildren();
    assertEquals(2, dateNodes.length);
    assertEquals(d1.getValue(), provider.getValue(dateNodes[0]));
    assertEquals(d2.getValue(), provider.getValue(dateNodes[1]));
  }

  @Test
  public void testInputChanged_clearsOldData() throws Exception {
    JavaDataDescriptor des = new JavaDataDescriptor(
        new LocalDate(), 1009823, METHOD_ID);
    
    page.getViewer().setInput(Arrays.asList(des));
    TreeNode root = provider.getRoot();
    assertFalse(root.getChildren() == null || root.getChildren().length == 0);
    try {
      provider.inputChanged(page.getViewer(), null, null);
      assertTrue(root.getChildren() == null || root.getChildren().length == 0);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testInputChanged_newInputNull() {
    try {
      provider.inputChanged(page.getViewer(), null, null);
    } catch (Exception e) {
      fail();
    }
  }
  
  @Test
  public void testInputChanged_invalidInput() {
    try {
      provider.inputChanged(page.getViewer(), new Object(), new Object());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetSelectedCategories_emptyArray() {
    try {
      ICategory[] cats = new ICategory[] { JavaCategory.TYPE, JavaCategory.PACKAGE };
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories(new ICategory[0]);
      // The defaults:
      cats = new ICategory[] {
          JavaCategory.PROJECT, 
          JavaCategory.PACKAGE_ROOT, 
          JavaCategory.PACKAGE, 
          JavaCategory.TYPE_ROOT,
          JavaCategory.TYPE,
          JavaCategory.METHOD
      };
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetSelectedCategories_emptyVararg() {
    try {
      ICategory[] cats = new ICategory[] { JavaCategory.TYPE, JavaCategory.PACKAGE };
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories();
      // The defaults:
      cats = new ICategory[] { 
          JavaCategory.PROJECT, 
          JavaCategory.PACKAGE_ROOT, 
          JavaCategory.PACKAGE, 
          JavaCategory.TYPE_ROOT,
          JavaCategory.TYPE,
          JavaCategory.METHOD 
      };
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetPaintCategory() {
    // Two data descriptor of different dates, same element, different value:
    JavaDataDescriptor d1 = new JavaDataDescriptor(
        new LocalDate(), 1009823, METHOD_ID);
    JavaDataDescriptor d2 = new JavaDataDescriptor(
        d1.getDate().minusDays(1), 123, METHOD_ID);

    page.getViewer().setInput(Arrays.asList(d1, d2));

    provider.setSelectedCategories(JavaCategory.DATE);
    provider.setPaintCategory(JavaCategory.DATE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(JavaCategory.TYPE);
    provider.setPaintCategory(JavaCategory.TYPE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(JavaCategory.TYPE_ROOT);
    provider.setPaintCategory(JavaCategory.TYPE_ROOT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(JavaCategory.METHOD);
    provider.setPaintCategory(JavaCategory.METHOD);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(JavaCategory.PACKAGE);
    provider.setPaintCategory(JavaCategory.PACKAGE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(JavaCategory.PROJECT);
    provider.setPaintCategory(JavaCategory.PROJECT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    
    provider.setSelectedCategories(JavaCategory.PACKAGE_ROOT);
    provider.setPaintCategory(JavaCategory.PACKAGE_ROOT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
  }

  @Test
  public void testSetSelectedCategories() {
    ICategory[] cats = new ICategory[] { JavaCategory.TYPE, JavaCategory.PACKAGE };
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());

    cats = new ICategory[] { JavaCategory.TYPE, JavaCategory.DATE, JavaCategory.PROJECT };
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());
  }

  @Test
  public void testShouldFilter() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode projectNode = new TreeNode(project);
    TreeNode packageRootNode = new TreeNode(packageRoot);
    TreeNode packageNode = new TreeNode(packageFragment);
    TreeNode typeRootNode = new TreeNode(typeRoot);
    TreeNode typeNode = new TreeNode(type);
    TreeNode methodNode = new TreeNode(method);
    
    provider.setSelectedCategories(JavaCategory.DATE);
    assertFalse(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(packageRootNode));
    assertTrue(provider.shouldFilter(packageNode));
    assertTrue(provider.shouldFilter(typeRootNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(methodNode));

    provider.setSelectedCategories(JavaCategory.PROJECT);
    assertTrue(provider.shouldFilter(dateNode));
    assertFalse(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(packageRootNode));
    assertTrue(provider.shouldFilter(packageNode));
    assertTrue(provider.shouldFilter(typeRootNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(methodNode));

    provider.setSelectedCategories(JavaCategory.PACKAGE_ROOT);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertFalse(provider.shouldFilter(packageRootNode));
    assertTrue(provider.shouldFilter(packageNode));
    assertTrue(provider.shouldFilter(typeRootNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(methodNode));

    provider.setSelectedCategories(JavaCategory.PACKAGE);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(packageRootNode));
    assertFalse(provider.shouldFilter(packageNode));
    assertTrue(provider.shouldFilter(typeRootNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(methodNode));

    provider.setSelectedCategories(JavaCategory.TYPE_ROOT);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(packageRootNode));
    assertTrue(provider.shouldFilter(packageNode));
    assertFalse(provider.shouldFilter(typeRootNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(methodNode));

    provider.setSelectedCategories(JavaCategory.TYPE);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(packageRootNode));
    assertTrue(provider.shouldFilter(packageNode));
    assertTrue(provider.shouldFilter(typeRootNode));
    assertFalse(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(methodNode));

    provider.setSelectedCategories(JavaCategory.METHOD);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(packageRootNode));
    assertTrue(provider.shouldFilter(packageNode));
    assertTrue(provider.shouldFilter(typeRootNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertFalse(provider.shouldFilter(methodNode));
  }

  @Test
  public void testShouldPaint() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode projectNode = new TreeNode(project);
    TreeNode packageRootNode = new TreeNode(packageRoot);
    TreeNode packageNode = new TreeNode(packageFragment);
    TreeNode typeRootNode = new TreeNode(typeRoot);
    TreeNode typeNode = new TreeNode(type);
    TreeNode methodNode = new TreeNode(method);
    
    provider.setPaintCategory(JavaCategory.DATE);
    assertTrue(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(packageRootNode));
    assertFalse(provider.shouldPaint(packageNode));
    assertFalse(provider.shouldPaint(typeRootNode));
    assertFalse(provider.shouldPaint(typeNode));
    assertFalse(provider.shouldPaint(methodNode));
    
    provider.setPaintCategory(JavaCategory.PROJECT);
    assertFalse(provider.shouldPaint(dateNode));
    assertTrue(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(packageRootNode));
    assertFalse(provider.shouldPaint(packageNode));
    assertFalse(provider.shouldPaint(typeRootNode));
    assertFalse(provider.shouldPaint(typeNode));
    assertFalse(provider.shouldPaint(methodNode));
    
    provider.setPaintCategory(JavaCategory.PACKAGE_ROOT);
    assertFalse(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertTrue(provider.shouldPaint(packageRootNode));
    assertFalse(provider.shouldPaint(packageNode));
    assertFalse(provider.shouldPaint(typeRootNode));
    assertFalse(provider.shouldPaint(typeNode));
    assertFalse(provider.shouldPaint(methodNode));
    
    provider.setPaintCategory(JavaCategory.PACKAGE);
    assertFalse(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(packageRootNode));
    assertTrue(provider.shouldPaint(packageNode));
    assertFalse(provider.shouldPaint(typeRootNode));
    assertFalse(provider.shouldPaint(typeNode));
    assertFalse(provider.shouldPaint(methodNode));
    
    provider.setPaintCategory(JavaCategory.TYPE_ROOT);
    assertFalse(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(packageRootNode));
    assertFalse(provider.shouldPaint(packageNode));
    assertTrue(provider.shouldPaint(typeRootNode));
    assertFalse(provider.shouldPaint(typeNode));
    assertFalse(provider.shouldPaint(methodNode));
    
    provider.setPaintCategory(JavaCategory.TYPE);
    assertFalse(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(packageRootNode));
    assertFalse(provider.shouldPaint(packageNode));
    assertFalse(provider.shouldPaint(typeRootNode));
    assertTrue(provider.shouldPaint(typeNode));
    assertFalse(provider.shouldPaint(methodNode));
    
    provider.setPaintCategory(JavaCategory.METHOD);
    assertFalse(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(packageRootNode));
    assertFalse(provider.shouldPaint(packageNode));
    assertFalse(provider.shouldPaint(typeRootNode));
    assertFalse(provider.shouldPaint(typeNode));
    assertTrue(provider.shouldPaint(methodNode));
  }
}
