package rabbit.core.internal.storage.xml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
		DatatypeUtilTest.class,
		DataStoreTest.class,
		CommandEventStorerTest.class,
		FileEventStorerTest.class,
		JaxbUtilTest.class,
		PartEventStorerTest.class,
		PerspectiveEventStorerTest.class,
		XmlFileMapperTest.class,
//
})
public class TestsOthers {

}
