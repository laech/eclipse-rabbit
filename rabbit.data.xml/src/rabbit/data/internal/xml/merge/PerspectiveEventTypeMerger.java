package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

import com.google.common.base.Objects;

/**
 * Merger for {@link PerspectiveEventType}.
 */
public class PerspectiveEventTypeMerger extends
    AbstractMerger<PerspectiveEventType> {

  @Override
  protected boolean doIsMergeable(PerspectiveEventType t1,
      PerspectiveEventType t2) {
    return Objects.equal(t1.getPerspectiveId(), t2.getPerspectiveId());
  }

  @Override
  protected PerspectiveEventType doMerge(PerspectiveEventType main,
      PerspectiveEventType tmp) {
    main.setDuration(main.getDuration() + tmp.getDuration());
    return main;
  }

}
