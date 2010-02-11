package rabbit.core.internal.trackers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
		CommandTrackerTest.class,
		FileTrackerTest.class,
		PartTrackerTest.class,
		PerspectiveTrackerTest.class,
//
})
public class AllTests {
}
