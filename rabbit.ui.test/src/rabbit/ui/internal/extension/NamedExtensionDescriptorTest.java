package rabbit.ui.internal.extension;

import rabbit.ui.internal.extension.NamedExtensionDescriptor;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Test case for {@link NamedExtensionDescriptor}.
 * <p/>
 * To test the subclasses of a {@link NamedExtensionDescriptor}, extend this
 * test case and override {@link #create(String, String)} to return the
 * subclassed instance, all tests of this test case are performed on an instance
 * returned by {@link #create(String, String)}.
 * <p/>
 * You should disable {@link #compareToComparesByName()} if the subclass under
 * test does not compare by name.
 */
public class NamedExtensionDescriptorTest extends ExtensionDescriptorTest {

  private final String id = "a.b.c";
  private final String name = "Hello";

  private NamedExtensionDescriptor descriptor;

  @Override
  public void setup() {
    super.setup();
    descriptor = create(id, name);
  }

  @Test
  public void getNameShouldReturnTheName() throws Exception {
    assertThat(descriptor.getName(), is(name));
  }

  @Test
  public void compareToShouldCompareByName() throws Exception {
    NamedExtensionDescriptor equal = create(id, name);
    assertThat(descriptor.compareTo(equal), is(0));

    NamedExtensionDescriptor bigger = create(id, "z" + name);
    assertThat(descriptor.compareTo(bigger) < 0, is(true));

    NamedExtensionDescriptor smaller = create(id, " " + name);
    assertThat(descriptor.compareTo(smaller) > 0, is(true));
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowExceptionIfNameIsNull() throws Exception {
    create(id, null);
  }

  @Override
  protected final NamedExtensionDescriptor create(String id) {
    return create(id, name);
  }

  /**
   * Creates an {@link NamedExtensionDescriptor} for testing. Override this to
   * return a subclass for testing.
   * 
   * @param id the ID of the descriptor
   * @param name the name of the descriptor
   * @return an {@link NamedExtensionDescriptor} constructed with {@code id} and
   *         {@code name}
   */
  protected NamedExtensionDescriptor create(String id, String name) {
    return new NamedExtensionDescriptor(id, name);
  }
}
