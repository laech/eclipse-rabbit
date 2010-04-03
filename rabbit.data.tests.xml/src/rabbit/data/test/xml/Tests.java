package rabbit.data.test.xml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { DataStoreTest.class, //
    DatatypeUtilTest.class,//
    JaxbUtilTest.class,//
    XmlFileMapperTest.class,//
    XmlPluginTest.class,//
    //
})
public class Tests {

}
