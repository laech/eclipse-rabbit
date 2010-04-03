package rabbit.mylyn.tests.ui.pages;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { TaskPageContentProviderTest.class, //
    TaskPageDecoratingLabelProviderTest.class, //
    TaskPageLabelProviderTest.class, //
    TaskPageTest.class, //
    TaskResourceTest.class, //
})
public class AllTests {

}
