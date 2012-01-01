package rabbit.ui.internal.extension;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Test;

public final class CategoryDescriptorTest extends NamedExtensionDescriptorTest {

  private final String id = "my.id";
  private final String name = "my.name";

  /** A valid element for {@link CategoryDescriptor#from(IConfigurationElement)} */
  private IConfigurationElement element;

  @Override public void setup() {
    super.setup();
    element = mock(IConfigurationElement.class);
    given(element.getName()).willReturn(CategoryDescriptor.ELEMENT_NAME);
    given(element.getAttribute(CategoryDescriptor.ATTR_ID)).willReturn(id);
    given(element.getAttribute(CategoryDescriptor.ATTR_NAME)).willReturn(name);

    // Should throw no exceptions because element is valid
    CategoryDescriptor.from(element);
  }

  @Test public void isCategoryElementShouldReturnTrueForACategoryElement() {
    assertThat(CategoryDescriptor.isCategoryElement(element), is(true));
  }

  @Test public void isCategoryElementShouldReturnFalseForANonCategoryElement() {
    given(element.getName()).willReturn("a");
    assertThat(CategoryDescriptor.isCategoryElement(element), is(false));
  }

  @Test(expected = NullPointerException.class)//
  public void isCategoryElementShouldThrowExceptionIfElementIsNull() {
    CategoryDescriptor.isCategoryElement(null);
  }

  @Test(expected = NullPointerException.class)//
  public void fromShouldThrowsExceptionIfElementIsNull() {
    CategoryDescriptor.from(null);
  }

  @Test public void fromShouldThrowExceptionIfElementNameIsUnexpected() {
    given(element.getName()).willReturn("unexpected-name");

    try {
      CategoryDescriptor.from(element);
      fail();
    } catch (IllegalArgumentException e) {
      // Pass
    }
  }

  @Test public void fromShouldThrowExceptionIfIdIsNull() throws Exception {
    given(element.getAttribute(CategoryDescriptor.ATTR_ID)).willReturn(null);

    try {
      CategoryDescriptor.from(element);
      fail();
    } catch (IllegalArgumentException e) {
      // Pass
    }
  }

  @Test public void fromShouldThrowExceptionIfNameIsNull() throws Exception {
    given(element.getAttribute(CategoryDescriptor.ATTR_NAME)).willReturn(null);

    try {
      CategoryDescriptor.from(element);
      fail();
    } catch (IllegalArgumentException e) {
      // Pass
    }
  }

  @Test public void fromShouldConstructADescriptorUsingAttributesFromTheElement() {
    CategoryDescriptor descriptor = CategoryDescriptor.from(element);
    assertThat(descriptor.getId(), is(id));
    assertThat(descriptor.getName(), is(name));
  }

  @Override protected CategoryDescriptor create(String id, String name) {
    return new CategoryDescriptor(id, name);
  }
}
