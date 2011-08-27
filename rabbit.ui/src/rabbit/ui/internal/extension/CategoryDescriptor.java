package rabbit.ui.internal.extension;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Represents a page category.
 */
public final class CategoryDescriptor extends NamedExtensionDescriptor {

  /**
   * The name of a category element.
   */
  static final String ELEMENT_NAME = "category";

  /**
   * The ID attribute of a category element
   */
  static final String ATTR_ID = "id";

  /**
   * The name attribute of a category element
   */
  static final String ATTR_NAME = "name";

  /**
   * Creates a {@link CategoryDescriptor} from an {@link IConfigurationElement}.
   * 
   * @param element the element to create from
   * @throws NullPointerException if the element is null
   * @throws IllegalArgumentException if the element is not a category element
   *         or the element does not contain enough attributes
   * @return a {@link CategoryDescriptor}, not null
   */
  public static CategoryDescriptor from(IConfigurationElement element) {
    checkArgument(isCategoryElement(element),
        "element name is unexpected: " + element.getName());

    final String id = element.getAttribute(ATTR_ID);
    final String name = element.getAttribute(ATTR_NAME);
    try {
      return new CategoryDescriptor(id, name);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Checks whether the given element is a category element.
   * 
   * @param element the element to check
   * @return {@code true} is the element is a category element, {@code false}
   *         otherwise
   */
  public static boolean isCategoryElement(IConfigurationElement element) {
    checkNotNull(element, "element cannot be null");
    return ELEMENT_NAME.equals(element.getName());
  }

  /**
   * Constructs a new category.
   * 
   * @param id the ID of this category
   * @param name this name of this category
   * @throws NullPointerException if id or name is null
   */
  public CategoryDescriptor(String id, String name) {
    super(id, name);
  }
}
