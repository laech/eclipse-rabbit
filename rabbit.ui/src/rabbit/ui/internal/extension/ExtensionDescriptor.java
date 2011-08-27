package rabbit.ui.internal.extension;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * Base class of an extension descriptor that describes an extension.
 */
public class ExtensionDescriptor {

  private final String id;

  /**
   * Constructs a extension descriptor.
   * 
   * @param id the ID of this extension descriptor
   * @throws NullPointerException if id is null
   */
  public ExtensionDescriptor(String id) {
    this.id = checkNotNull(id, "id cannot be null");
  }

  /**
   * Gets the ID of this descriptor.
   * 
   * @return the ID of this descriptor, not null
   */
  public final String getId() {
    return id;
  }

  @Override
  public final int hashCode() {
    return getId().hashCode();
  }

  @Override
  public final boolean equals(Object that) {
    return (that instanceof ExtensionDescriptor)
        ? ((ExtensionDescriptor)that).getId().equals(getId())
        : false;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(getClass())
        .add("id", getId())
        .toString();
  }
}
