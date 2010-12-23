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
package rabbit.ui.internal.viewers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

/**
 * @see CompositeColumnLabelProvider
 */
public class CompositeColumnLabelProviderTest {

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfConstructedWithANullLabelProvider() throws Exception {
    create(new ILabelProvider[]{null});
  }
  
  @Test
  public void shouldCloneTheArrayOfLabelProviders() throws Exception {
    String text = "Hello";
    ILabelProvider theProvider = mock(ILabelProvider.class);
    given(theProvider.getText(this)).willReturn(text);
    
    ILabelProvider[] providerArray = {theProvider};
    CompositeColumnLabelProvider provider = create(providerArray);
    providerArray[0] = null;
    
    assertThat(provider.getText(this), equalTo(text));
  }

  @Test
  public void getTextShouldReturnTheTextFromTheInternalLabelProvider() throws Exception {
    String text = "Hello";
    Object element = new Object();
    ILabelProvider p = mock(ILabelProvider.class);
    given(p.getText(element)).willReturn(text);

    CompositeColumnLabelProvider provider = create(p);
    assertThat(provider.getText(element), equalTo(text));
  }

  @Test
  public void getImageShouldReturnTheImageFromTheInternalLabelProvider() throws Exception {
    Display display = new Display();
    try {
      Object element = new Object();
      Image image = new Image(display, 1, 1);
      ILabelProvider p = mock(ILabelProvider.class);
      given(p.getImage(element)).willReturn(image);

      CompositeColumnLabelProvider provider = create(p);
      assertThat(provider.getImage(element), equalTo(image));

    } finally {
      display.dispose();
    }
  }

  /**
   * @see CompositeColumnLabelProvider#CompositeColumnLabelProvider(ILabelProvider...)
   */
  private CompositeColumnLabelProvider create(ILabelProvider... providers) {
    return new CompositeColumnLabelProvider(providers);
  }
}
