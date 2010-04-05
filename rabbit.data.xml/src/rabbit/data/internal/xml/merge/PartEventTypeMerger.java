package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.PartEventType;

import com.google.common.base.Objects;

/**
 * Merger for {@link PartEventType}.
 */
public class PartEventTypeMerger extends AbstractMerger<PartEventType> {

  @Override
  protected boolean doIsMergeable(PartEventType t1, PartEventType t2) {
    return Objects.equal(t1.getPartId(), t2.getPartId());
  }

  @Override
  protected PartEventType doMerge(PartEventType main, PartEventType tmp) {
    main.setDuration(main.getDuration() + tmp.getDuration());
    return main;
  }

}
