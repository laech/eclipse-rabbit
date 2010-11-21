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
package rabbit.ui.internal.pages;

import rabbit.ui.internal.pages.CollectionContentProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Test for {@link CollectionContentProvider}
 */
public class CollectionContentProviderTest {

  private CollectionContentProvider provider = new CollectionContentProvider();
  private Collection<Object> emptyCollection = Collections.emptyList();
  private Collection<Object> notEmptyCollection = new ArrayList<Object>();

  @Before
  public void setUp() {
    notEmptyCollection.clear();
    notEmptyCollection.add(new Object());
    notEmptyCollection.add("");
  }

  @Test
  public void testGetChildren() {
    assertNotNull(provider.getChildren(emptyCollection));
    assertEquals(emptyCollection.size(),
        provider.getChildren(emptyCollection).length);

    assertNotNull(provider.getChildren(notEmptyCollection));
    assertEquals(notEmptyCollection.size(), provider
        .getChildren(notEmptyCollection).length);
  }

  @Test
  public void testGetElements() {
    assertNotNull(provider.getElements(emptyCollection));
    assertEquals(emptyCollection.size(),
        provider.getElements(emptyCollection).length);

    assertNotNull(provider.getElements(notEmptyCollection));
    assertEquals(notEmptyCollection.size(), provider
        .getElements(notEmptyCollection).length);
  }

  @Test
  public void testHasChildren() {
    assertTrue(provider.hasChildren(notEmptyCollection));
  }

}
