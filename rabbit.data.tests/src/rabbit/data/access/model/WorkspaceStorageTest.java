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
package rabbit.data.access.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * @see WorkspaceStorage
 */
public class WorkspaceStorageTest {
  
  private IPath workspacePath;
  private IPath storagePath;
  
  @Before
  public void before() {
    workspacePath = new Path("/a/b");
    storagePath = new Path("/d");
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAStoragePath() {
    new WorkspaceStorage(null, workspacePath);
  }
  
  @Test
  public void shouldAcceptIfConstructedWithoutAWorkspacePath() {
    new WorkspaceStorage(storagePath, null);
  }
  
  @Test
  public void shouldReturnTheStoragePath() {
    assertThat(
        new WorkspaceStorage(storagePath, workspacePath).getStoragePath(), 
        is(storagePath));
  }
  
  @Test
  public void shouldReturnTheWorkspacePath() {
    assertThat(
        new WorkspaceStorage(storagePath, workspacePath).getWorkspacePath(), 
        is(workspacePath));
  }
}
