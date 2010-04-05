package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.LaunchEventType;

import com.google.common.base.Objects;

/**
 * Merger for {@link LaunchEventType}.
 */
public class LaunchEventTypeMerger extends AbstractMerger<LaunchEventType> {

  @Override
  protected boolean doIsMergeable(LaunchEventType t1, LaunchEventType t2) {
    return Objects.equal(t1.getName(), t2.getName())
        && Objects.equal(t1.getLaunchModeId(), t2.getLaunchModeId())
        && Objects.equal(t1.getLaunchTypeId(), t2.getLaunchTypeId());
  }

  @Override
  protected LaunchEventType doMerge(LaunchEventType main, LaunchEventType tmp) {
    main.setCount(main.getCount() + tmp.getCount());
    main.setTotalDuration(main.getTotalDuration() + tmp.getTotalDuration());

    for (String fileId : tmp.getFileId()) {
      if (!main.getFileId().contains(fileId))
        main.getFileId().add(fileId);
    }
    return main;
  }

}
