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

import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;

/**
 * @see WorkspaceStorageLabelProvider
 */
public class WorkspaceStorageLabelProviderTest {

  private WorkspaceStorageLabelProvider labelProvider;

  @Before
  public void create() {
    labelProvider = new WorkspaceStorageLabelProvider();
  }

  @After
  public void dispose() {
    labelProvider.dispose();
  }
  
  @Test
  public void getForegroundShouldReturnAColorIfWorkspacePathIsNull() {
    IPath storage = Path.fromPortableString("/");
    WorkspaceStorage ws = new WorkspaceStorage(storage, null);
    assertThat(labelProvider.getForeground(ws), notNullValue());
  }

  @Test
  public void getTextShouldReturnTheWorkspacePathIfThereIsOne() {
    IPath workspace = new Path(System.getProperty("user.home"));
    IPath storage = Path.fromPortableString("/storage");
    WorkspaceStorage ws = new WorkspaceStorage(storage, workspace);
    
    String expected = format("%s (%s)", workspace.lastSegment(), workspace.toOSString());
    assertThat(labelProvider.getText(ws), equalTo(expected));
  }

  @Test
  public void getTextShouldReturnAHelpfulMessageIfThereIsNoWorkspacePath() {
    // Currently if the workspace is at: /home/user/workspace,
    // then the folder storing data for that workspace is named: .home.user.workspace,
    // which is done by replacing all the separators with a dot.
    IPath storage = Path.fromPortableString("/Rabbit/.Hello.World");
    WorkspaceStorage ws = new WorkspaceStorage(storage, null);
    
    String expected = format("Unknown (may be %s?)", storage.lastSegment().replace(".", "/"));
    assertThat(labelProvider.getText(ws), equalTo(expected));
  }
  
  @Test
  public void getImageShouldReturnANonnullImage() {
    IPath workspace = new Path(System.getProperty("user.home"));
    IPath storage = Path.fromPortableString("/storage");
    WorkspaceStorage ws = new WorkspaceStorage(storage, workspace);
    assertThat(labelProvider.getImage(ws), notNullValue());
  }
}
