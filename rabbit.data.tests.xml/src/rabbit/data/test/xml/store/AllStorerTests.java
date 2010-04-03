package rabbit.data.test.xml.store;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CommandEventStorerTest.class, //
    FileEventStorerTest.class, //
    LaunchEventStorerTest.class, //
    PartEventStorerTest.class, //
    PerspectiveEventStorerTest.class, //
})
public class AllStorerTests {

}
