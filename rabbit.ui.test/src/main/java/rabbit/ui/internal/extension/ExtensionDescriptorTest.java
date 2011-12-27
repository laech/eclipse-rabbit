package rabbit.ui.internal.extension;

import rabbit.ui.internal.extension.ExtensionDescriptor;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ExtensionDescriptor}.
 * <p/>
 * To test the subclasses of an {@link ExtensionDescriptor}, extend this test
 * case and override {@link #create(String)} to return the subclassed instance,
 * all tests of this test case are performed on an instance returned by
 * {@link #create(String)}.
 */
public class ExtensionDescriptorTest {

  private final String id = "a.b.c";

  private ExtensionDescriptor descriptor;

  @Before
  public void setup() {
    descriptor = create(id);
  }

  @Test
  public void getIdShouldReturnTheId() throws Exception {
    assertThat(descriptor.getId(), is(id));
  }

  @Test
  public void hashCodeShouldReturnHashCodeOfId() throws Exception {
    assertThat(descriptor.hashCode(), is(id.hashCode()));
  }

  @Test
  public void equalsShouldReturnTrueIfIdsAreEqual() throws Exception {
    ExtensionDescriptor descriptor2 = create(id);
    assertThat(descriptor.equals(descriptor2), is(true));
  }

  @Test
  public void equalsShouldReturnTrueIfSameObject() throws Exception {
    assertThat(descriptor.equals(descriptor), is(true));
  }

  @Test
  public void equalsShouldReturnFalseIfIdsAreNotEqual() throws Exception {
    ExtensionDescriptor descriptor2 = create(id + id);
    assertThat(descriptor.equals(descriptor2), is(false));
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowsExceptionIfIdIsNull() throws Exception {
    create(null);
  }

  /**
   * Creates an {@link ExtensionDescriptor} for testing. Override this to return
   * a subclass for testing.
   * 
   * @param id the ID of the descriptor
   * @return an {@link ExtensionDescriptor} constructed with {@code id}
   */
  protected ExtensionDescriptor create(String id) {
    return new ExtensionDescriptor(id);
  }
}
