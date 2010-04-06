package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.store.model.PartEvent;

/**
 * Converts from {@link PartEvent} to {@link PartEventType}.
 */
public class PartEventConverter extends
    AbstractConverter<PartEvent, PartEventType> {

  @Override
  protected PartEventType doConvert(PartEvent element) {
    PartEventType type = new PartEventType();
    type.setDuration(element.getDuration());
    type.setPartId(element.getWorkbenchPart().getSite().getId());
    return type;
  }

}
