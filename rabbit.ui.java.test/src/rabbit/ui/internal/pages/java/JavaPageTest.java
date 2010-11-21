package rabbit.ui.internal.pages.java;

import rabbit.ui.internal.pages.AbstractFilteredTreePage;
import rabbit.ui.internal.pages.AbstractFilteredTreePageTest;
import rabbit.ui.internal.pages.java.JavaCategory;
import rabbit.ui.internal.pages.java.JavaPage;
import rabbit.ui.internal.pages.java.JavaPageContentProvider;
import rabbit.ui.internal.util.ICategory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @see JavaPage
 */
public class JavaPageTest extends AbstractFilteredTreePageTest {

  @Test
  public void testSaveState_selectedCategories() throws Exception {
    JavaPageContentProvider contents = (JavaPageContentProvider) page
        .getViewer().getContentProvider();
    
    ICategory[] categories = new ICategory[] { JavaCategory.DATE, JavaCategory.PACKAGE };
    contents.setSelectedCategories(categories);
    saveState(page);
    
    contents.setSelectedCategories(new ICategory[] { JavaCategory.METHOD });
    restoreState(page);
    assertArrayEquals(categories, contents.getSelectedCategories());
  }
  
  @Test
  public void testSaveState_paintCategory() throws Exception {
    JavaPageContentProvider contents = (JavaPageContentProvider) page
        .getViewer().getContentProvider();
    
    JavaCategory paintCategory = JavaCategory.PACKAGE_ROOT;
    contents.setPaintCategory(paintCategory);
    saveState(page);
    
    contents.setPaintCategory(JavaCategory.DATE);
    restoreState(page);
    assertEquals(paintCategory, contents.getPaintCategory());
  }

  @Override
  protected AbstractFilteredTreePage createPage() {
    return new JavaPage();
  }
}
