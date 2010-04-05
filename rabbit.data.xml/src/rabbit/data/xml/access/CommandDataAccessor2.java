package rabbit.data.xml.access;

import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.CommandEventListType;
import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.internal.xml.schema.events.EventListType;

import org.joda.time.LocalDate;

import java.util.Collection;

public class CommandDataAccessor2
    extends
    AbstractDataNodeAccessor<CommandDataDescriptor, CommandEventType, CommandEventListType> {

  @Override
  protected CommandDataDescriptor createDataNode(LocalDate cal,
      CommandEventType type) {

    try {
      return new CommandDataDescriptor(cal, type.getCount(), type
          .getCommandId());
      
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<CommandEventType> getXmlTypes(CommandEventListType list) {
    return list.getCommandEvent();
  }

  @Override
  protected Collection<CommandEventListType> getCategories(EventListType doc) {
    return doc.getCommandEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.COMMAND_STORE;
  }

}
