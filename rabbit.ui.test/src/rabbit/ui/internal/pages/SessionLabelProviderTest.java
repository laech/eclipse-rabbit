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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Test for {@link SessionLabelProvider}
 */
public class SessionLabelProviderTest {

  private static final SessionLabelProvider provider;
  private static final DateLabelProvider dateLabels;

  static {
    provider = new SessionLabelProvider();
    dateLabels = new DateLabelProvider();
  }

  @AfterClass
  public static void afterClass() {
    provider.dispose();
    dateLabels.dispose();
  }

  @Test
  public void getImageShouldReturnANonnullImage() {
    assertThat(provider.getImage(new LocalDate()), notNullValue());
  }

  @Test
  public void getTextShouldReturnTheRightText() {
    LocalDate today = new LocalDate();
    assertThat(provider.getText(today), equalTo(dateLabels.getText(today)));

    LocalDate yesterday = today.minusDays(1);
    assertThat(provider.getText(yesterday), equalTo(dateLabels.getText(yesterday)));

    LocalDate someday = yesterday.minusDays(1);
    assertThat(provider.getText(someday), equalTo(dateLabels.getText(someday)));
  }
}
