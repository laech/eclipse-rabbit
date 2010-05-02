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
package rabbit.data.internal.xml;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Activator class for this plug-in.
 */
public class XmlPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "rabbit.data.xml";

  private static XmlPlugin plugin;

  /**
   * The default location of the storage root.
   */
  private static final IPath DEFAULT_STORAGE_ROOT = Path.fromOSString(
      System.getProperty("user.home")).append("Rabbit");

  /**
   * Constant string to use with a java.util.Properties to get/set the storage
   * root.
   */
  private static final String PROP_STORAGE_ROOT = "storage.root";

  public static XmlPlugin getDefault() {
    return plugin;
  }

  public XmlPlugin() {
  }

  /**
   * Gets the full path to the storage location of this workspace. The returned
   * path should not be cached because it is changeable.
   * 
   * @return The full path to the storage location folder.
   */
  public IPath getStoragePath() {
    String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation()
        .toOSString();
    workspace = workspace.replace(File.separatorChar, '.');
    workspace = workspace.replace(":", "");
    return getStoragePathRoot().append(workspace);
  }

  /**
   * Gets the root of the storage location. The returned path should not be
   * cached because it's changeable.
   * 
   * @return The path to the root of the storage location.
   */
  public IPath getStoragePathRoot() {
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(getPropertiesFile());
      Properties prop = new Properties();
      prop.load(stream);
      return Path.fromOSString(prop.getProperty(PROP_STORAGE_ROOT,
          DEFAULT_STORAGE_ROOT.toOSString()));

    } catch (FileNotFoundException e) {
      return resetStoragePathRoot();

    } catch (IOException e) {
      return resetStoragePathRoot();

    } catch (IllegalArgumentException e) {
      return resetStoragePathRoot();

    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      }
    }
  }

  /**
   * Gets the paths to all the workspace storage locations for this plug-in.
   * Includes {@link #getStoragePath()}. The returned paths should not be cached
   * because they are changeable.
   * 
   * @return The paths to all the workspace storage locations
   */
  public IPath[] getStoragePaths() {
    IPath root = getStoragePathRoot();
    File rootFile = root.toFile();
    File[] files = rootFile.listFiles();
    if (files == null) {
      return new IPath[0];
    }

    List<IPath> paths = new ArrayList<IPath>(files.length);
    for (File file : files) {
      if (file.isDirectory()) {
        paths.add(Path.fromOSString(file.getAbsolutePath()));
      }
    }

    return paths.toArray(new IPath[paths.size()]);
  }

  /**
   * Sets the storage root.
   * 
   * @param directory The new storage root.
   * @return true if the setting is applied; false if any of the followings is
   *         true:
   *         <ul>
   *         <li>The directory does not exist.</li>
   *         <li>The directory cannot be read from.</li>
   *         <li>The directory cannot be written to.</li>
   *         <li>If error occurs while saving the setting.</li>
   *         </ul>
   * @throws NullPointerException If parameter is null.
   */
  public boolean setStoragePathRoot(File directory) {
    if (directory == null) {
      throw new IllegalArgumentException();
    }

    if (!directory.exists() || !directory.canRead() || !directory.canWrite()) {
      return false;
    }

    FileOutputStream stream = null;
    try {
      stream = new FileOutputStream(getPropertiesFile());
      Properties prop = new Properties();
      prop.setProperty(PROP_STORAGE_ROOT, directory.getAbsolutePath());
      prop.store(stream,
          "This file contains configurations for the Rabbit Eclipse plugin."
              + "\nPlease do not delete, otherwise Rabbit will not work properly.");
      return true;

    } catch (FileNotFoundException e) {
      return false;

    } catch (IOException e) {
      return false;

    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Gets the properties file for saving the storage root property.
   */
  private File getPropertiesFile() {
    String str = System.getProperty("user.home") + File.separator
        + ".rabbit.properties";
    return new File(str);
  }

  /**
   * Resets the storage root.
   * 
   * @return The default storage root.
   */
  private IPath resetStoragePathRoot() {
    setStoragePathRoot(DEFAULT_STORAGE_ROOT.toFile());
    return DEFAULT_STORAGE_ROOT;
  }
}
