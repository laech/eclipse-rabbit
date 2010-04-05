package rabbit.data.xml.access;

import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;

import org.joda.time.LocalDate;

import java.util.Collection;

public class LaunchDataAccessor2
    extends
    AbstractDataNodeAccessor<LaunchDataDescriptor, LaunchEventType, LaunchEventListType> {

  @Override
  protected LaunchDataDescriptor createDataNode(LocalDate cal,
      LaunchEventType type) {

    try {
      LaunchConfigurationDescriptor des = new LaunchConfigurationDescriptor(
          type.getName(), type.getLaunchModeId(), type.getLaunchTypeId());

      return new LaunchDataDescriptor(cal, des, type.getCount(), type
          .getTotalDuration(), type.getFileId());
      
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<LaunchEventType> getXmlTypes(LaunchEventListType list) {
    return list.getLaunchEvent();
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
