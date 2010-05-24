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
package rabbit.data.test.xml;

import rabbit.data.internal.xml.XmlPlugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Test for {@link XmlPlugin}
 */
@SuppressWarnings("restriction")
public class XmlPluginTest {

  private static XmlPlugin plugin = XmlPlugin.getDefault();

  @Test
  public void testGetStoragePath() {
    assertNotNull(plugin.getStoragePath());
    assertTrue(plugin.getStoragePath().toFile().exists());
    assertTrue(plugin.getStoragePath().toFile().isDirectory());
  }

  @Test
  public void testGetStoragePathRoot() {
    assertNotNull(plugin.getStoragePathRoot());
  }

  @Test
  public void testPluginId() {
    assertEquals(XmlPlugin.PLUGIN_ID, plugin.getBundle().getSymbolicName());
  }

  @Test
  public void testSetStoragePathRoot() throws IOException {
    IPath oldPath = plugin.getStoragePathRoot();
    IPath path = oldPath.append(System.currentTimeMillis() + "");

    File file = path.toFile();

    // File not exist, should return false:
    assertFalse(file.exists());
    assertFalse(plugin.setStoragePathRoot(file));

    // File exists, readable, writable, should return true:
    assertTrue(file.mkdirs());
    assertTrue(file.setReadable(true));
    assertTrue(file.setWritable(true));
    assertTrue(plugin.setStoragePathRoot(file));

    plugin.setStoragePathRoot(oldPath.toFile());
  }

  @Test
  public void testStoragePaths() {
    Set<IPath> paths = new HashSet<IPath>();
    for (IPath path : plugin.getStoragePaths()) {
      paths.add(path);
    }
    boolean hasDefaultPath = false;
    for (IPath path : paths) {
      if (path.toString().equals(plugin.getStoragePath().toString())) {
        hasDefaultPath = true;
        break;
      }
    }
    if (!hasDefaultPath) {
      fail();
    }

    IPath root = plugin.getStoragePathRoot();
    File rootFile = new File(root.toOSString());
    File[] files = rootFile.listFiles();
    paths = new HashSet<IPath>(files.length);
    for (File file : files) {
      if (file.isDirectory()) {
        paths.add(Path.fromOSString(file.getAbsolutePath()));
      }

      System.out.println(file.getName());
    }
    assertEquals(paths.size(), plugin.getStoragePaths().length);
    for (IPath path : plugin.getStoragePaths()) {
      assertTrue(paths.contains(path));
    }
  }
}
