package rabbit.data.xml;

import rabbit.data.internal.xml.StoreNamesModule;
import rabbit.data.internal.xml.convert.ConverterModule;
import rabbit.data.internal.xml.merge.MergerModule;
import rabbit.data.internal.xml.store.StorerModule;

import com.google.inject.AbstractModule;

/**
 * The module of this plug-in.
 */
public class XmlModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new StoreNamesModule());
    install(new ConverterModule());
    install(new MergerModule());
    install(new StorerModule());
    install(new rabbit.data.internal.xml.access.AccessorModule()); // to be removed
    install(new rabbit.data.internal.xml.access2.AccessorModule());
  }
}
