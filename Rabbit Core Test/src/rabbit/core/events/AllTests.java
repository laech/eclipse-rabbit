package rabbit.core.events;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
		CommandEventTest.class,
		ContinuousEventTest.class,
		DiscreteEventTest.class,
		PartEventTest.class,
		PerspectiveEventTest.class,
//
})
public class AllTests {
}
