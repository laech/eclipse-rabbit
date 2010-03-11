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
package rabbit.core.internal;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Test;

import rabbit.core.RabbitCore;

/**
 * Test for {@link PreferenceInitializer}
 */
public class PreferenceInitializerTest {

	private PreferenceInitializer pref = new PreferenceInitializer();

	@Test
	public void testInitializePreference() {
		IPreferenceStore store = RabbitCore.getDefault().getPreferenceStore();
		store.setDefault(RabbitCore.STORAGE_LOCATION, "");

		pref.initializeDefaultPreferences();
		Assert.assertFalse(store.getDefaultString(RabbitCore.STORAGE_LOCATION).equals(""));
	}
}
