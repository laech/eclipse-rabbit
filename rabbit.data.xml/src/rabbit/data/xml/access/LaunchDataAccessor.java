package rabbit.data.xml.access;

import static rabbit.data.internal.xml.util.StringUtil.getString;

import rabbit.data.access.model.LaunchDescriptor;
import rabbit.data.internal.xml.AbstractAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gets launch event data.
 */
public class LaunchDataAccessor
    extends
    AbstractAccessor<Set<LaunchDescriptor>, LaunchEventType, LaunchEventListType> {

  @Override
  protected Set<LaunchDescriptor> filter(List<LaunchEventListType> data) {
    Set<LaunchDescriptor> result = new HashSet<LaunchDescriptor>();

    for (LaunchEventListType list : data) {
      for (LaunchEventType type : list.getLaunchEvent()) {

        boolean done = false;
        for (LaunchDescriptor des : result) {
          if (getString(type.getName()).equals(des.getLaunchName())
              && getString(type.getLaunchModeId())
                  .equals(des.getLaunchModeId())
              && getString(type.getLaunchTypeId())
                  .equals(des.getLaunchTypeId())) {

            des.setCount(des.getCount() + type.getCount());
            des.setTotalDuration(des.getTotalDuration()
                + type.getTotalDuration());
            des.getFileIds().addAll(type.getFileId());

            done = true;
            break;
          }
        }

        if (!done) {
          LaunchDescriptor launch = new LaunchDescriptor();
          launch.setTotalDuration(type.getTotalDuration());
          launch.getFileIds().addAll(type.getFileId());
          launch.setLaunchName(type.getName());
          launch.setLaunchTypeId(type.getLaunchTypeId());
          launch.setLaunchModeId(type.getLaunchModeId());
          launch.setCount(type.getCount());

          result.add(launch);
        }
      }
    }
    return result;
  }

  @Override
  protected Collection<LaunchEventListType> getCategories(EventListType doc) {
    return doc.getLaunchEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.LAUNCH_STORE;
  }

}
