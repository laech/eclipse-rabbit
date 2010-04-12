/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.ui.tests.pages;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { // 
//

CollectionContentProviderTest.class, //

    // Command page related:
    CommandPageContentProviderTest.class, //
    CommandPageLabelProviderTest.class, //
    CommandPageTest.class, //

    // Part page related:
    PartPageContentProviderTest.class, //
    PartPageLabelProviderTest.class, //
    PartPageTest.class, //

    // Perspective page related:
    PerspectivePageLabelProviderTest.class, //
    PerspectivePageContentProviderTest.class, //
    PerspectivePageTest.class, //

    // Resource page related:
    ResourcePageTest.class, //
    ResourcePageContentProviderTest.class, //
    ResourcePageLabelProviderTest.class, //
    ResourcePageDecoratingLabelProviderTest.class, //

    // Session page related:
    SessionPageLabelProviderTest.class, //
    SessionPageTest.class, //

    // Launch page related:
    LaunchPageContentProviderTest.class, // 
    LaunchPageLabelProviderTest.class, //
    LaunchPageTest.class, //

    //
    WorkbenchPartLabelProviderTest.class, //
    LocalDateLabelProviderTest.class, //
    CommandLabelProviderTest.class, //
})
public class AllTests {

}
