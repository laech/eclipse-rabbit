package rabbit.core.storage.xml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
		CommandDataAccessorTest.class,
		FileDataAccessorTest.class,
		PartDataAccessorTest.class,
		PerspectiveDataAccessorTest.class,
		SessionDataAccessorTest.class,
//
})
public class AllTests {
}
