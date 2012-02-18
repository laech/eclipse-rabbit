/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.tracking.internal.workbench;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static rabbit.tracking.internal.workbench.ListenerExtension.extension;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.ITrackerEventListener;
import rabbit.tracking.workbench.ICommandEventListener;
import rabbit.tracking.workbench.IFileEventListener;

public final class ListenerExtensionTest {

  private static final Object[] EMPTY_ARRAY = new Object[0];
  private static final IConfigurationElement[] EMPTY_ELEMENT = new IConfigurationElement[0];

  private String tagFilter;
  private Class<ICommandEventListener> classFilter;
  private ICommandEventListener classInstance;
  private IConfigurationElement element;
  private IConfigurationElement[] elements;
  private List<Exception> errorsBucket;

  private ListenerExtension<ICommandEventListener> extension;

  @Before public void setup() throws Exception {
    tagFilter = "abc";
    errorsBucket = newArrayList();
    classFilter = ICommandEventListener.class;
    classInstance = mock(classFilter);
    element = mock(IConfigurationElement.class);
    given(element.getName()).willReturn(tagFilter);
    given(element.createExecutableExtension("class")).willReturn(classInstance);
    elements = new IConfigurationElement[]{element};

    extension = create(tagFilter, classFilter);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfTagIsNull() {
    create(null, ITrackerEventListener.class);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfClassIsNull() {
    create(tagFilter, null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsExceptionIfElementsIsNull() {
    extension.load(null, errorsBucket);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsExceptionIfErrorsBucketIsNull() {
    extension.load(EMPTY_ELEMENT, null);
  }

  @Test public void returnEmptySetIfNoObjectWasCreated() {
    Set<?> set = extension.load(EMPTY_ELEMENT, errorsBucket);
    assertThat(set, is(notNullValue()));
    assertThat(set.toArray(), is(EMPTY_ARRAY));
  }

  @Test public void returnCreatedObjectsThatMatchTagAndClassFilter()
      throws Exception {

    Set<ICommandEventListener> objs = extension(tagFilter, classFilter)
        .load(elements, errorsBucket);

    assertThat(objs.size(), is(1));
    assertThat(objs.iterator().next(), is(classInstance));
  }

  @Test public void putsErrorsIntoErrorsBucket() {
    Exception exception = new RuntimeException();
    given(element.getName()).willThrow(exception);

    extension.load(elements, errorsBucket);
    assertThat(errorsBucket.size(), is(1));
    assertThat(errorsBucket.iterator().next(), is(exception));
  }

  @Test public void ignoresElementsThatPassesTagFilterButFailedClassFilter()
      throws Exception {

    Set<IFileEventListener> objs = extension(tagFilter,
        IFileEventListener.class).load(elements, errorsBucket);

    assertThat(objs.toArray(), is(EMPTY_ARRAY));
    assertThat(errorsBucket.size(), is(1)); // Failed expected class is an error
  }

  @Test public void ignoresElementsThatPassesClassFilterButFailedTagFilter()
      throws Exception {

    IConfigurationElement[] elements = {element};
    Set<ICommandEventListener> objs = extension("notAMatch", classFilter)
        .load(elements, errorsBucket);

    assertThat(objs.toArray(), is(EMPTY_ARRAY));
    assertThat(errorsBucket.size(), is(0)); // Failed tag filter is not an error
  }

  private <T extends ITrackerEventListener<?>> ListenerExtension<T> create(
      String tag, Class<T> clazz) {
    return extension(tag, clazz);
  }
}
