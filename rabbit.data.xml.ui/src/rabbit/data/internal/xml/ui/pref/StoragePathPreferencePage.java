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
package rabbit.data.internal.xml.ui.pref;

import rabbit.data.internal.xml.XmlPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StoragePathPreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage {

  private Text storageText;

  public StoragePathPreferencePage() {
  }

  public StoragePathPreferencePage(String title) {
    super(title);
  }

  public StoragePathPreferencePage(String title, ImageDescriptor image) {
    super(title, image);
  }

  @Override
  public void init(IWorkbench workbench) {
  }

  @Override
  public boolean performOk() {

    if (!XmlPlugin.getDefault().getStoragePathRoot().toOSString().equals(
        storageText.getText())) {

      File newRoot = new File(storageText.getText().trim());

      // Make sure the directory exists:
      if (!newRoot.exists() && !newRoot.mkdirs()) {
        setErrorMessage("Error creating the new directory.");
        return false;
      }

      // Try to create folder to see if we have write permission:
      File tmp = new File(newRoot, "." + System.nanoTime());
      if (!tmp.mkdir()) {
        setErrorMessage("Error writing to the selected directory.");
        return false;
      }
      tmp.delete();

      File oldRoot = XmlPlugin.getDefault().getStoragePathRoot().toFile();
      if (oldRoot.list().length > 0
          && MessageDialog.openQuestion(getShell(), "Copy Exsiting Data?",
              "Would you like to copy the existing data "
                  + "over to the new storage location for Rabbit?")) {
        try {
          copyDirectory(oldRoot, newRoot);
        } catch (IOException e) {
          ErrorDialog.openError(getShell(), "Error Copying Files", e
              .getMessage(), new Status(IStatus.ERROR, XmlPlugin.PLUGIN_ID, e
              .getMessage(), e));
        }
      }

      XmlPlugin.getDefault().setStoragePathRoot(newRoot);
    }

    return true;
  }

  @Override
  protected Control createContents(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    Composite cmp = new Composite(parent, SWT.NONE);
    cmp.setLayout(layout);
    cmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // Contains settings for storage location:
    Group pathGroup = new Group(cmp, SWT.NONE);
    pathGroup.setText("Location to Store Data");
    pathGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    pathGroup.setLayout(new GridLayout(3, false));
    {
      Label description = new Label(pathGroup, SWT.WRAP);
      description
          .setText("Please use a dedicated folder to prevent Rabbit from messing "
              + "up your files.\nIt's a rabbit after all!");
      GridDataFactory.fillDefaults().span(3, 1).applyTo(description);

      new Label(pathGroup, SWT.NONE).setText("Location:");

      storageText = new Text(pathGroup, SWT.BORDER);
      storageText.setText(XmlPlugin.getDefault().getStoragePathRoot()
          .toOSString());
      storageText
          .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      storageText.addListener(SWT.KeyUp, new Listener() {
        @Override
        public void handleEvent(Event event) {
          setErrorMessage(null);
        }
      });

      Button browse = new Button(pathGroup, SWT.PUSH);
      browse.setText("    Browse...    ");
      browse.addListener(SWT.Selection, new Listener() {
        @Override
        public void handleEvent(Event event) {
          DirectoryDialog dialog = new DirectoryDialog(getShell());
          dialog
              .setMessage("Select a folder for storing data collected by Rabbit.");

          String path = dialog.open();
          if (!path.toLowerCase().endsWith("rabbit")) {
            if (!path.endsWith(File.separator)) {
              path += File.separator;
            }
            path += "Rabbit";
          }
          storageText.setText(path);
          setErrorMessage(null);
        }
      });
    }
    return cmp;
  }

  @Override
  protected void performDefaults() {
    storageText.setText(XmlPlugin.getDefault().getStoragePathRoot()
        .toOSString());
    super.performDefaults();
  }

  /**
   * Copies everything in the source directory to the destination.
   * 
   * @param source The source directory.
   * @param destination The destination directory.
   * @throws IOException If errors occur while moving a file/directory.
   */
  private void copyDirectory(File source, File destination) throws IOException {
    if (source.isDirectory()) {
      if (!destination.exists()) {
        if (!destination.mkdirs()) {
          throw new IOException(
              "Cannot create folder, perhaps no write permission?");
        }
      }

      String[] children = source.list();
      for (int i = 0; i < children.length; i++) {
        copyDirectory(new File(source, children[i]), new File(destination,
            children[i]));
      }
    } else {

      InputStream in = new FileInputStream(source);
      OutputStream out = new FileOutputStream(destination);

      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      in.close();
      out.close();
    }
  }
}
