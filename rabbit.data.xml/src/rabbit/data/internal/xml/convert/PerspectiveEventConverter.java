package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;

/**
 * Converts from {@link PerspectiveEvent} to {@link PerspectiveEventType}.
 */
public class PerspectiveEventConverter extends
    AbstractConverter<PerspectiveEvent, PerspectiveEventType> {

  @Override
  protected PerspectiveEventType doConvert(PerspectiveEvent element) {
    PerspectiveEventType type = new PerspectiveEventType();
    type.setDuration(element.getDuration());
    type.setPerspectiveId(element.getPerspective().getId());
    return type;
  }

}
