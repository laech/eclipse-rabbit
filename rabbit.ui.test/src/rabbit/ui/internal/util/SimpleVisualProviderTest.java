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
package rabbit.ui.internal.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

public final class SimpleVisualProviderTest {

  @Test
  public void initialCategoryIsNullable() {
    assertThat(new SimpleVisualProvider(null).getVisualCategory(),
        is(nullValue()));
  }

  @Test
  public void categoryIsNullByDefault() {
    assertThat(new SimpleVisualProvider().getVisualCategory(), is(nullValue()));
  }

  @Test
  public void ableToSetTheCategory() {
    ICategory category = mock(ICategory.class);
    SimpleVisualProvider provider = new SimpleVisualProvider();
    provider.setVisualCategory(category);
    assertThat(provider.getVisualCategory(), is(category));
  }

  @Test
  public void notifiesObserversWhenChangedToADifferentCategory() {
    ICategory category1 = mock(ICategory.class);
    ICategory category2 = mock(ICategory.class);
    SimpleVisualProvider provider = new SimpleVisualProvider(category1);

    final boolean[] called = {false};
    provider.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        called[0] = true;
      }
    });
    provider.setVisualCategory(category2);
    assertThat(called[0], is(true));
  }

  @Test
  public void doesNotNotifyObserversWhenChangedToSameCategory() {
    ICategory category = mock(ICategory.class);
    SimpleVisualProvider provider = new SimpleVisualProvider(category);

    final boolean[] called = {false};
    provider.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        called[0] = true;
      }
    });
    provider.setVisualCategory(category);
    assertThat(called[0], is(false));
  }
}
