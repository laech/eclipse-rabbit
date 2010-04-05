package rabbit.data.test.xml.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CommandEventTypeMergerTest.class, //
    FileEventTypeMergerTest.class, //
    LaunchEventTypeMergerTest.class, //
    PartEventTypeMergerTest.class, //
    PerspectiveEventTypeMergerTest.class, //
})
public class AllMergerTests {

}
