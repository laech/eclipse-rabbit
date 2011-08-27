package rabbit.ui.internal.extension;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class CategoryDescriptorTest extends NamedExtensionDescriptorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private final String id = "my.id";
  private final String name = "my.name";

  /** A valid element for {@link CategoryDescriptor#from(IConfigurationElement)} */
  private IConfigurationElement element;

  @Override
  public void setup() {
    super.setup();
    element = mock(IConfigurationElement.class);
    given(element.getName()).willReturn(CategoryDescriptor.ELEMENT_NAME);
    given(element.getAttribute(CategoryDescriptor.ATTR_ID)).willReturn(id);
    given(element.getAttribute(CategoryDescriptor.ATTR_NAME)).willReturn(name);

    // Should throw no exceptions because element is valid
    CategoryDescriptor.from(element);
  }

  @Test
  public void isCategoryElementShouldReturnTrueForACategoryElement() {
    assertThat(CategoryDescriptor.isCategoryElement(element), is(true));
  }

  @Test
  public void isCategoryElementShouldReturnFalseForANonCategoryElement() {
    given(element.getName()).willReturn("a");
    assertThat(CategoryDescriptor.isCategoryElement(element), is(false));
  }

  @Test
  public void isCategoryElementShouldThrowExceptionIfElementIsNull() {
    thrown.expect(NullPointerException.class);
    CategoryDescriptor.isCategoryElement(null);
  }

  @Test
  public void fromShouldThrowsExceptionIfElementIsNull() {
    thrown.expect(NullPointerException.class);
    CategoryDescriptor.from(null);
  }

  @Test
  public void fromShouldThrowExceptionIfElementNameIsUnexpected() {
    given(element.getName()).willReturn("unexpected-name");

    thrown.expect(IllegalArgumentException.class);
    CategoryDescriptor.from(element);
  }

  @Test
  public void fromShouldThrowExceptionIfIdIsNull() throws Exception {
    given(element.getAttribute(CategoryDescriptor.ATTR_ID)).willReturn(null);

    thrown.expect(IllegalArgumentException.class);
    CategoryDescriptor.from(element);
  }

  @Test
  public void fromShouldThrowExceptionIfNameIsNull() throws Exception {
    given(element.getAttribute(CategoryDescriptor.ATTR_NAME)).willReturn(null);

    thrown.expect(IllegalArgumentException.class);
    CategoryDescriptor.from(element);
  }

  @Test
  public void fromShouldConstructADescriptorUsingAttributesFromTheElement() {
    CategoryDescriptor descriptor = CategoryDescriptor.from(element);
    assertThat(descriptor.getId(), is(id));
    assertThat(descriptor.getName(), is(name));
  }

  @Override
  protected CategoryDescriptor create(String id, String name) {
    return new CategoryDescriptor(id, name);
  }
}
