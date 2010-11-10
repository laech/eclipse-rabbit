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
package rabbit.data.internal.access.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for this package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    CommandDataTest.class,
    FileDataTest.class,
    JavaDataTest.class,
    KeyMapBuilderTest.class,
    LaunchDataTest.class,
    PartDataTest.class,
    PerspectiveDataTest.class,
    SessionDataTest.class,
    TaskDataTest.class})
public class AllTests {

}
