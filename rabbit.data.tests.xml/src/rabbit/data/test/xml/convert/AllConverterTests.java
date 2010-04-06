package rabbit.data.test.xml.convert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CommandEventConverterTest.class, //
    FileEventConverterTest.class, //
    LaunchEventConverterTest.class, //
    PartEventConverterTest.class, //
    PerspectiveEventConverterTest.class, //
})
public class AllConverterTests {

}
