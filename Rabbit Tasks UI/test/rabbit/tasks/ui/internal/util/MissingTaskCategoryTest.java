/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.tasks.ui.internal.util;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import rabbit.tasks.core.TaskId;

/**
 * @see MissingTaskCategory
 */
public class MissingTaskCategoryTest {

	@Test
	public void testGetChildren() {
		MissingTaskCategory cat = MissingTaskCategory.getCategory();
		try {
			cat.getChildren().add(new MissingTask(new TaskId("id", new Date())));
		} catch (Exception e) {
			fail();
		}
	}
}
