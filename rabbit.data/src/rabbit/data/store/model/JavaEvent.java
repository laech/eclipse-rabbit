package rabbit.data.store.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jdt.core.IJavaElement;
import org.joda.time.DateTime;

// TODO test
public class JavaEvent extends ContinuousEvent {
  
  private IJavaElement element;

  public JavaEvent(DateTime endTime, long duration, IJavaElement element) {
    super(endTime, duration);
    checkNotNull(element);
    this.element = element;
  }

  public IJavaElement getElement() {
    return element;
  }
}
