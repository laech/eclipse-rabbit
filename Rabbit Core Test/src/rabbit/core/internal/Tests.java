package rabbit.core.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
		// Not include (SWTBot test): IdleDetectorTest.class,
		PreferenceInitializerTest.class,
		ResourceDataAttacherTest.class,
		TrackerObjectTest.class,
//
})
public class Tests {

}
