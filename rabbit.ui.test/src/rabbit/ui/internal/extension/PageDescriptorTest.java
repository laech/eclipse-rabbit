package rabbit.ui.internal.extension;

import static rabbit.ui.internal.extension.PageDescriptor.ATTR_CATEGORY_ID;
import static rabbit.ui.internal.extension.PageDescriptor.ATTR_CLASS;
import static rabbit.ui.internal.extension.PageDescriptor.ATTR_ICON_PATH;
import static rabbit.ui.internal.extension.PageDescriptor.ATTR_ID;
import static rabbit.ui.internal.extension.PageDescriptor.ATTR_NAME;
import static rabbit.ui.internal.extension.PageDescriptor.ELEMENT_NAME;

import rabbit.ui.IPage;
import rabbit.ui.test.Constants;
import rabbit.ui.test.util.EmptyPage;

import static org.eclipse.core.runtime.IStatus.OK;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public final class PageDescriptorTest extends NamedExtensionDescriptorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private final String id = "a.b.c";
  private final String name = "Hello";
  private final EmptyPage page = new EmptyPage();
  private final ImageDescriptor icon = mock(ImageDescriptor.class);
  private final String categoryId = "category";
  private PageDescriptor descriptor;

  /**
   * A valid element for
   * {@link PageDescriptor#from(IConfigurationElement, String)}
   */
  private IConfigurationElement element;

  @Override
  public void setup() {
    super.setup();
    descriptor = create(id, name, page, icon, categoryId);
    
    IContributor contributor = mock(IContributor.class);
    given(contributor.getName()).willReturn(Constants.PLUGIN_ID);

    element = mock(IConfigurationElement.class);
    given(element.getContributor()).willReturn(contributor);
    given(element.getName()).willReturn(ELEMENT_NAME);
    given(element.getAttribute(ATTR_ID)).willReturn(id);
    given(element.getAttribute(ATTR_NAME)).willReturn(name);
    given(element.getAttribute(ATTR_CATEGORY_ID)).willReturn(categoryId);
    given(element.getAttribute(ATTR_ICON_PATH)).willReturn(null);
    given(element.getAttribute(ATTR_CLASS)).willReturn(
        EmptyPage.class.getName());
    try {
      given(element.createExecutableExtension(ATTR_CLASS)).willReturn(page);
    } catch (CoreException e) {
      throw new AssertionError(e.getMessage());
    }
  }

  @Test
  public void checkTestHasBeenSetupCorrectly() throws Exception {
    // Make sure there is no exception will be thrown
    PageDescriptor.from(element);
  }

  @Test
  public void fromShouldAcceptPluginIdAsNull() throws Exception {
    PageDescriptor.from(element);
    // No exception
  }

  @Test
  public void fromShouldConstructADescriptorUsingAttributesFromTheElement() {
    PageDescriptor page = PageDescriptor.from(element);
    assertThat(page.getCategoryId(), is(categoryId));
    assertThat(page.getId(), is(id));
    assertThat(page.getName(), is(name));
    assertThat(page.getPage(), is(notNullValue()));
    assertThat(page.getPage(), is(instanceOf(EmptyPage.class)));
  }

  @Test
  public void fromShouldThrowExceptionIfElementNameIsUnexpected() {
    given(element.getName()).willReturn("unexpected");
    thrown.expect(IllegalArgumentException.class);
    PageDescriptor.from(element);
  }

  @Test
  public void fromShouldThrowExceptionIfIdIsNull() {
    given(element.getAttribute(ATTR_ID)).willReturn(null);
    thrown.expect(IllegalArgumentException.class);
    PageDescriptor.from(element);
  }

  @Test
  public void fromShouldThrowExceptionIfNameIsNull() {
    given(element.getAttribute(ATTR_NAME)).willReturn(null);
    thrown.expect(IllegalArgumentException.class);
    PageDescriptor.from(element);
  }

  @Test
  public void fromShouldThrowExceptionIfPageClassIsNull() throws Exception {
    given(element.getAttribute(ATTR_CLASS)).willReturn(null);
    given(element.createExecutableExtension(ATTR_CLASS)).willReturn(null);
    thrown.expect(IllegalArgumentException.class);
    PageDescriptor.from(element);
  }

  @Test
  public void fromShouldThrowExeptionIfPageClassCannotBeInstantiated()
      throws Exception {
    given(element.getAttribute(ATTR_CLASS)).willReturn("invalid.class.name");
    given(element.createExecutableExtension(ATTR_CLASS)).willAnswer(
        new Answer<Void>() {
          @Override
          public Void answer(InvocationOnMock invocation) throws Throwable {
            throw new CoreException(new Status(OK, Constants.PLUGIN_ID,
                "no msg"));
          }
        });
    thrown.expect(IllegalArgumentException.class);
    PageDescriptor.from(element);
  }

  @Test
  public void fromShouldThrowNoExceptionIfCategoryIdIsNull() {
    given(element.getAttribute(ATTR_CATEGORY_ID)).willReturn(null);
    PageDescriptor.from(element);
    // No exception
  }

  @Test
  public void fromShouldThrowNoExceptionIfIconPathIsNull() throws Exception {
    given(element.getAttribute(ATTR_ICON_PATH)).willReturn(null);
    PageDescriptor.from(element);
    // No exception
  }

  @Test
  public void isPageElementShouldReturnTrueForAPageElement() {
    assertThat(PageDescriptor.isPageElement(element), is(true));
  }

  @Test
  public void isPageElementShouldReturnFalseForANonPageElement() {
    given(element.getName()).willReturn("z");
    assertThat(PageDescriptor.isPageElement(element), is(false));
  }

  @Test
  public void isPageElementShouldThrowExceptionIsElementIsNull() {
    thrown.expect(NullPointerException.class);
    PageDescriptor.isPageElement(null);
  }

  @Test
  public void getPageShouldReturnThePage() {
    assertThat(descriptor.getPage(), is((IPage)page));
  }

  @Test
  public void getIconShouldReturnTheIcon() {
    assertThat(descriptor.getIcon(), is(icon));
  }

  @Test
  public void getCategoryIdShouldReturnTheCategoryId() {
    assertThat(descriptor.getCategoryId(), is(categoryId));
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowExceptionIfPageIsNull() {
    create(id, name, null, icon, categoryId);
  }

  @Test
  public void constructorShouldThrowNoExceptionIfIconIsNull() {
    create(id, name, page, null, categoryId);
  }

  @Override
  protected PageDescriptor create(String id, String name) {
    return create(id, name, page, icon, categoryId);
  }

  private PageDescriptor create(String id, String name, IPage page,
      ImageDescriptor icon, String categoryId) {
    return new PageDescriptor(id, name, page, icon, categoryId);
  }
}
