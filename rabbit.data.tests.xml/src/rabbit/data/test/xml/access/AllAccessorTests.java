package rabbit.data.test.xml.access;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CommandDataAccessorTest.class, //
    FileDataAccessorTest.class, //
    LaunchDataAccessorTest.class, //
    PartDataAccessorTest.class, //
    PerspectiveDataAccessorTest.class, //
    SessionDataAccessorTest.class, //
})
public class AllAccessorTests {

}
