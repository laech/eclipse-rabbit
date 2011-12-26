package rabbit.ui.internal.extension;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents an extension descriptor with a name.
 */
public class NamedExtensionDescriptor extends ExtensionDescriptor
    implements Comparable<NamedExtensionDescriptor> {

  private final String name;

  /**
   * Constructs a new descriptor.
   * 
   * @param id the ID of this descriptor
   * @param name this name of this descriptor
   * @throws NullPointerException if id or name is null
   */
  public NamedExtensionDescriptor(String id, String name) {
    super(id);
    this.name = checkNotNull(name, "name cannot be null");
  }

  /**
   * Gets the name of this descriptor.
   * 
   * @return the name of this descriptor, not null
   */
  public final String getName() {
    return name;
  }

  @Override
  public int compareTo(NamedExtensionDescriptor that) {
    return getName().compareTo(that.getName());
  }
}
