package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import org.eclipse.core.runtime.CoreException;

/**
 * Converts from {@link LaunchEvent} to {@link LaunchEventType}.
 */
public class LaunchEventConverter extends
    AbstractConverter<LaunchEvent, LaunchEventType> {

  @Override
  protected LaunchEventType doConvert(LaunchEvent element) {
    LaunchEventType type = new LaunchEventType();
    type.getFileId().addAll(element.getFileIds());
    type.setTotalDuration(element.getDuration());
    type.setName(element.getLaunchConfiguration().getName());
    try {
      type.setLaunchTypeId(element.getLaunchConfiguration().getType()
          .getIdentifier());
    } catch (CoreException ex) {
      System.out.println(getClass() + ": " + ex.getMessage());
      type.setLaunchTypeId(null);
    }
    type.setLaunchModeId(element.getLaunch().getLaunchMode());
    type.setCount(1);

    return type;
  }

}
