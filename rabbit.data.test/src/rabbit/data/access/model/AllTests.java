package rabbit.data.access.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {//
CommandDataDescriptorTest.class, //
    DateDescriptorTest.class, //
    FileDataDescriptorTest.class, //
    LaunchConfigurationDescriptorTest.class, //
    LaunchDataDescriptorTest.class, //
    PartDataDescriptorTest.class, //
    PerspectiveDataDescriptorTest.class, //
    SessionDataDescriptorTest.class, //
    TaskFileDataDescriptorTest.class,
    JavaDataDescriptorTest.class,
    ValueDescriptorTest.class, //
})
public class AllTests {

}
